#include "websocket/handler/MessageHandler.h"
#include "websocket/packet/ByteBuf.h"
#include "websocket/packet/Packet.h"
#include "websocket/handler/PacketHandler.h"
#include "websocket/packet/c2s/C2SPacketChat.h"
#include "websocket/packet/c2s/C2SPacketKeepalive.h"
#include "websocket/packet/s2c/S2CPacketKeepalive.h"
#include "util/ByteUtil.h"
#include "util/DateTimeUtil.h"
#include "util/crypt/ChaCha20.h"
#include "util/crypt/HS256.h"
#include "../../../Utils/Logger.h"
#include "Main.h"
#include <websocketpp/client.hpp>
#include <websocketpp/config/asio_no_tls_client.hpp>
#include <chrono>
#include <thread>
#include <utility>

namespace team::cool::client::websocket::handler {

MessageHandler* MessageHandler::instance = nullptr;

MessageHandler::MessageHandler(std::string uri, std::string token)
        : uri(std::move(uri)), token(std::move(token)) {
    instance = this;

    client.init_asio();
    client.clear_access_channels(websocketpp::log::alevel::all);
    client.clear_error_channels(websocketpp::log::elevel::all);

    client.set_message_handler([this](websocketpp::connection_hdl hdl, const message_ptr& msg) {
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

        LOGI("消息通道 WebSocket 连接已建立");

        if (cbCopy) {
            cbCopy();
        }

        running = true;
        keepaliveThread = std::thread([this]() {
            packet::s2c::S2CPacketKeepalive::reset();

            while (running) {

                bool stillConnected;
                {
                    std::lock_guard<std::mutex> lock(connectionMutex);
                    stillConnected = connected;
                }
                if (!stillConnected) return;

                try {
                    auto keepalive = std::make_shared<packet::c2s::C2SPacketKeepalive>();
                    PacketHandler::send(this, keepalive);
                } catch (...) {
                    LOGE("发送Keepalive时异常");
                }

                if (util::DateTimeUtil::expiry(packet::s2c::S2CPacketKeepalive::lastActive,
                                               util::DateTimeUtil::KEEPALIVE)) {
                    LOGW("保活超时，连接关闭");
                    // 通知主模块心跳超时
                    team::cool::client::Main::setStatus(
                        team::cool::client::VerifyStatus::HEARTBEAT_TIMEOUT, "心跳包超时");
                    close();
                    return;
                }

                if (packet::s2c::S2CPacketKeepalive::ids.size() >= 30) {
                    packet::s2c::S2CPacketKeepalive::ids.clear();
                }

                std::this_thread::sleep_for(std::chrono::seconds(2));
            }
        });

//        inputThread = std::thread([this]() {
//            std::string user_input;
//            while (running) {
//                if (!std::getline(std::cin, user_input)) {
//                    std::cerr << "\n❌ 输入流读取失败或结束。" << std::endl;
//                    break;
//                }
//
//                if (isOpen()) {
//                    PacketHandler::send(this, std::make_shared<packet::c2s::C2SPacketChat>(user_input));
//                }
//            }
//        });
    });

    client.set_close_handler([this](websocketpp::connection_hdl hdl) {
        std::function<void(int, const std::string&)> cbCopy;
        {
            std::lock_guard<std::mutex> lock(connectionMutex);
            connected = false;
            cbCopy = closeCallback;
        }

        websocketpp::close::status::value code = websocketpp::close::status::normal;
        std::string reason;
        try {
            auto con = client.get_con_from_hdl(hdl);
            code = con->get_remote_close_code();
            reason = con->get_remote_close_reason();
        } catch (...) {}

        if (cbCopy) {
            cbCopy(code, reason);
        }
    });

    client.set_fail_handler([this](websocketpp::connection_hdl hdl) {
        std::function<void(const std::string&)> cbCopy;
        {
            std::lock_guard<std::mutex> lock(connectionMutex);
            connected = false;
            cbCopy = errorCallback;
        }

        std::string error;
        try {
            auto con = client.get_con_from_hdl(hdl);
            error = con->get_ec().message();
        } catch (...) {}

        if (cbCopy) {
            cbCopy(error);
        }
    });
}

MessageHandler::~MessageHandler() {
    running = false;

    if (keepaliveThread.joinable())
        keepaliveThread.join();

//    if (inputThread.joinable())
//        inputThread.join();

    close();

    if (ioThread.joinable()) {
        client.stop();
        ioThread.join();
    }

    if (instance == this)
        instance = nullptr;
}

void MessageHandler::connect(const std::string& uri, const std::string& token) {
    if (instance && instance->connected) {
        LOGW("消息处理器已连接");
        return;
    }

    if (!instance) {
        instance = new MessageHandler(uri, token);
    } else {
        instance->uri = uri;
        instance->token = token;
    }

    instance->doConnect();
}

void MessageHandler::doConnect() {
    LOGI("连接消息通道: %s", uri.c_str());

    websocketpp::lib::error_code ec;
    auto con = client.get_connection(uri, ec);

    if (ec) {
        if (errorCallback) errorCallback(ec.message());
        return;
    }

    if (!token.empty()) {
        con->append_header("tk", token);
    }

    client.connect(con);

    ioThread = std::thread([this]() {
        client.run();
    });
}

void MessageHandler::close() {
    LOGI("正在关闭与消息通道的连接...");
    websocketpp::connection_hdl hdlCopy;
    bool shouldClose = false;

    {
        std::lock_guard<std::mutex> lock(connectionMutex);
        if (connected) {
            connected = false;
            hdlCopy = connectionHandle;
            shouldClose = true;
        }
    }

    if (!shouldClose) return;

    try {
        client.close(hdlCopy, websocketpp::close::status::normal, "客户端关闭");
    } catch (...) {}

    LOGI("连接关闭");
}

bool MessageHandler::isOpen() const {
    return connected;
}

void MessageHandler::onOpen(std::function<void()> callback) {
    openCallback = std::move(callback);
}

void MessageHandler::onMessage(const std::vector<uint8_t>& message) {
    try {
        // 原样保留
        packet::ByteBuf buf(message);
        int8_t packetType = static_cast<int8_t>(buf.readByte());
        std::vector<uint8_t> nonce = buf.readBytes(12);
        std::string sign = buf.readString();
        std::vector<uint8_t> payload = buf.readBytes(buf.readInt());

        if (util::crypt::HS256::getKey().empty()) return;
        if (!util::crypt::HS256::verify(payload, sign)) return;

        auto dataAndTime = util::ByteUtil::split(payload, payload.size() - 8);
        int64_t time = util::ByteUtil::bytesToLong(dataAndTime.second);

        if (util::DateTimeUtil::expiry(time)) return;

        std::vector<uint8_t> decrypted = util::crypt::ChaCha20::decrypt(nonce, dataAndTime.first);
        packet::ByteBuf packetBuf(decrypted);

        auto packet = PacketHandler::packet(packetType);
        packet->setClient(this);
        packet->read(packetBuf);
        packet->handle();

    } catch (const std::exception& e) {
        LOGE("处理消息时出错: %s", e.what());
    }
}

void MessageHandler::onClose(std::function<void(int, const std::string&)> callback) {
    closeCallback = std::move(callback);
}

void MessageHandler::onError(std::function<void(const std::string&)> callback) {
    errorCallback = std::move(callback);
}

void MessageHandler::send(const std::vector<uint8_t>& data) {
    websocketpp::connection_hdl hdlCopy;
    bool shouldSend = false;

    {
        std::lock_guard<std::mutex> lock(connectionMutex);
        if (connected) {
            hdlCopy = connectionHandle;
            shouldSend = true;
        }
    }

    if (!shouldSend) return;

    client.get_io_service().post([this, hdlCopy, data]() {
        websocketpp::lib::error_code ec;
        client.send(hdlCopy, data.data(), data.size(),
                    websocketpp::frame::opcode::binary, ec);
        if (ec) {
            LOGE("发送数据失败: %s", ec.message().c_str());
        }
    });
}

} // namespace team::cool::client::websocket::handler

