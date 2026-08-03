#include "websocket/handler/LoginHandler.h"
#include "websocket/packet/ByteBuf.h"
#include "websocket/packet/Packet.h"
#include "websocket/handler/PacketHandler.h"
#include "util/ByteUtil.h"
#include "util/DateTimeUtil.h"
#include "util/crypt/HS256.h"
#include "../../../Utils/Logger.h"
#include <websocketpp/client.hpp>
#include <websocketpp/config/asio_no_tls_client.hpp>
#include <stdexcept>
#include <utility>

namespace team::cool::client::websocket::handler {

LoginHandler* LoginHandler::instance = nullptr;

//#define DEBUG_LOCK(msg) std::cout << "[DEBUG][TID " << std::this_thread::get_id() << "] " << msg << std::endl;

LoginHandler::LoginHandler(std::string uri) : uri(std::move(uri)) {
    instance = this;

    client.init_asio();
    client.clear_access_channels(websocketpp::log::alevel::all);
    client.clear_error_channels(websocketpp::log::elevel::all);

    client.set_message_handler([this](websocketpp::connection_hdl hdl, message_ptr msg) {
        if (msg->get_opcode() == websocketpp::frame::opcode::binary) {
            const auto& payload = msg->get_payload();
            std::vector<uint8_t> message(payload.begin(), payload.end());
            this->onMessage(message);
        }
    });

    client.set_open_handler([this](websocketpp::connection_hdl hdl) {
        std::function<void()> cbCopy;
        {
            std::lock_guard<std::mutex> lock(connectionMutex);

            connectionHandle = hdl;
            connected = true;
            cbCopy = openCallback;
        }

        retryCount.store(0);  // 连接成功，重置重试计数

        LOGI("WebSocket 连接已建立");

        if (cbCopy) {
            cbCopy();
        }
    });

    client.set_close_handler([this](websocketpp::connection_hdl hdl) {
        std::function<void(int, const std::string&)> cbCopy;
        websocketpp::connection_hdl hdlCopy = hdl;
        {
            std::lock_guard<std::mutex> lock(connectionMutex);
            connected = false;
            cbCopy = closeCallback;
        }

        websocketpp::close::status::value code = websocketpp::close::status::normal;
        std::string reason;
        try {
            auto con = client.get_con_from_hdl(hdlCopy);
            code = con->get_remote_close_code();
            reason = con->get_remote_close_reason();
        } catch (...) {}

        if (cbCopy) {
            cbCopy(code, reason);
        }
    });

    client.set_fail_handler([this](websocketpp::connection_hdl hdl) {
        if (stopping.load()) return;  // 析构中，不重连

        std::string errorMsg;
        try {
            auto con = client.get_con_from_hdl(hdl);
            errorMsg = con->get_ec().message();
        } catch (...) {
            errorMsg = "Unknown error";
        }

        {
            std::lock_guard<std::mutex> lock(connectionMutex);
            connected = false;
        }

        int currentRetry = retryCount.fetch_add(1);

        if (currentRetry < MAX_RETRIES) {
            LOGI("连接失败: %s, 第 %d/%d 次重试...",
                 errorMsg.c_str(), currentRetry + 1, MAX_RETRIES);

            // 在新线程中重连，不阻塞 ASIO IO 线程
            std::thread([this]() {
                if (!stopping.load()) {
                    reconnect();
                }
            }).detach();
        } else {
            LOGE("连接最终失败: %s (已重试 %d 次)", errorMsg.c_str(), MAX_RETRIES);

            std::function<void(const std::string&)> cbCopy;
            {
                std::lock_guard<std::mutex> lock(connectionMutex);
                cbCopy = errorCallback;
            }
            if (cbCopy) {
                cbCopy(errorMsg + " (已重试 " + std::to_string(MAX_RETRIES) + " 次)");
            }
        }
    });
}

LoginHandler::~LoginHandler() {
    stopping = true;  // 标记正在停止，阻止重连

    if (connected) {
        close(1000, "Handler destroyed");
    }

    if (ioThread.joinable()) {
        client.stop();
        ioThread.join();
    }

    if (instance == this)
        instance = nullptr;
}

void LoginHandler::connect() {
    LOGI("尝试建立连接: %s", uri.c_str());

    websocketpp::lib::error_code ec;
    auto con = client.get_connection(uri, ec);

    if (ec) {
        LOGE("创建连接失败: %s", ec.message().c_str());
        if (errorCallback) errorCallback(ec.message());
        return;
    }

    // 设置连接超时（5秒）
    con->set_open_handshake_timeout(5000);

    client.connect(con);

    ioThread = std::thread([this]() {
        try {
            client.run();
        } catch (const std::exception& e) {
            LOGE("IO 线程异常: %s", e.what());
        }
    });
}

void LoginHandler::close(int code, const std::string& reason) {
    std::lock_guard<std::mutex> lock(connectionMutex);

    if (connected) {
        try {
            client.close(connectionHandle, code, reason);
        } catch (const std::exception& e) {
            LOGE("关闭连接时出错: %s", e.what());
        }
        connected = false;
    }
}

bool LoginHandler::isOpen() const {
    return connected;
}

void LoginHandler::onOpen(std::function<void()> callback) {
    openCallback = std::move(callback);
}

void LoginHandler::onMessage(const std::vector<uint8_t>& message) {
    try {
        packet::ByteBuf buf(message);

        int8_t packetType = static_cast<int8_t>(buf.readByte());
        std::string sign = buf.readString();
        std::vector<uint8_t> payload = buf.readBytes(buf.readInt());

        if (packetType != static_cast<int8_t>(11)) { // -245
            if (util::crypt::HS256::getKey().empty()) return;
            if (!util::crypt::HS256::verify(payload, sign)) {
                LOGE("签名效验失败");
                return;
            }
        }

        auto dataAndTime = util::ByteUtil::split(payload, payload.size() - 8);
        int64_t time = util::ByteUtil::bytesToLong(dataAndTime.second);

        if (util::DateTimeUtil::expiry(time)) {
            LOGE("时间戳效验失败，Time: %lld", static_cast<long long>(time));
            return;
        }

        packet::ByteBuf packetBuf(dataAndTime.first);

        auto packet = PacketHandler::packet(packetType);
        if (!packet) {
            LOGE("未找到包");
            return;
        }
        packet->setClient(this);
        packet->read(packetBuf);
        packet->handle();
    } catch (const std::exception& e) {
        LOGE("处理消息时出错: %s", e.what());
    }
}

void LoginHandler::onClose(std::function<void(int, const std::string&)> callback) {
    closeCallback = std::move(callback);
}

void LoginHandler::onError(std::function<void(const std::string&)> callback) {
    errorCallback = std::move(callback);
}

void LoginHandler::send(const std::vector<uint8_t>& data) {
    websocketpp::connection_hdl hdlCopy;
    bool shouldSend = false;

    {
        std::lock_guard<std::mutex> lock(connectionMutex);

        if (connected) {
            hdlCopy = connectionHandle;
            shouldSend = true;
        }
    }

    if (!shouldSend) {
        return;
    }

    try {
        client.get_io_service().post([this, hdlCopy, data]() {
            websocketpp::lib::error_code ec;
            client.send(hdlCopy, data.data(), data.size(), websocketpp::frame::opcode::binary, ec);
            if (ec) {
                LOGE("IO 线程发送数据失败: %s", ec.message().c_str());
            }
        });
    } catch (const std::exception& e) {
        LOGE("post 发送任务时出错: %s", e.what());
    }
}

void LoginHandler::reconnect() {
    if (stopping.load()) return;

    try {
        // 停止当前 IO 服务
        client.stop();

        // 等待旧线程结束
        if (ioThread.joinable()) {
            ioThread.join();
        }

        if (stopping.load()) return;

        // 重置 ASIO
        client.reset();
        client.init_asio();

        // 重新设置日志级别
        client.clear_access_channels(websocketpp::log::alevel::all);
        client.clear_error_channels(websocketpp::log::elevel::all);

        // 重新连接
        websocketpp::lib::error_code ec;
        auto con = client.get_connection(uri, ec);

        if (ec) {
            LOGE("重试创建连接失败: %s", ec.message().c_str());
            return;
        }

        // 设置连接超时（5秒）
        con->set_open_handshake_timeout(5000);

        client.connect(con);

        ioThread = std::thread([this]() {
            try {
                client.run();
            } catch (const std::exception& e) {
                LOGE("IO 线程异常: %s", e.what());
            }
        });

    } catch (const std::exception& e) {
        LOGE("重连异常: %s", e.what());
    }
}

} // namespace team::cool::client::websocket::handler

