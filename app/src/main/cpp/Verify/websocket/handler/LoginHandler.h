#pragma once

#define ASIO_STANDALONE

#include <string>
#include <functional>
#include <cstdint>
#include <vector>
#include <memory>
#include <thread>
#include <atomic>
#include <mutex>
#include <websocketpp/client.hpp>
#include <websocketpp/config/asio_no_tls_client.hpp>

typedef websocketpp::client<websocketpp::config::asio_client> wsclient;

using websocketpp::lib::bind;

typedef websocketpp::config::asio_client::message_type::ptr message_ptr;

namespace team::cool::client::websocket::handler {

class LoginHandler {
private:
    wsclient client;
    websocketpp::connection_hdl connectionHandle;
    std::string uri;
    std::thread ioThread;
    std::atomic<bool> connected{false};
    std::atomic<bool> stopping{false};  // 防止析构期间重连
    std::atomic<int> retryCount{0};
    static constexpr int MAX_RETRIES = 5;
    std::mutex connectionMutex;
    
    std::function<void()> openCallback;
    std::function<void(int, const std::string&)> closeCallback;
    std::function<void(const std::string&)> errorCallback;

public:
    explicit LoginHandler(std::string  uri);
    ~LoginHandler();
    
    void connect();
    void close(int code, const std::string& reason);
    bool isOpen() const;
    
    void onOpen(std::function<void()> callback);
    void onMessage(const std::vector<uint8_t>& message);
    void onClose(std::function<void(int, const std::string&)> callback);
    void onError(std::function<void(const std::string&)> callback);
    
    void send(const std::vector<uint8_t>& data);
    void reconnect();  // 重连方法

    static LoginHandler* instance;
};

} // namespace team::cool::client::websocket::handler

