#pragma once

#define ASIO_STANDALONE

#include <string>
#include <vector>
#include <functional>
#include <thread>
#include <atomic>
#include <memory>
#include <mutex>
#include <websocketpp/client.hpp>
#include <websocketpp/config/asio_no_tls_client.hpp>

typedef websocketpp::client<websocketpp::config::asio_client> wsclient;

using websocketpp::lib::bind;

typedef websocketpp::config::asio_client::message_type::ptr message_ptr;

namespace team::cool::client::websocket::handler {

class MessageHandler {
private:
    wsclient client;
    websocketpp::connection_hdl connectionHandle;
    std::string uri;
    std::string token;
    static MessageHandler* instance;
    std::thread keepaliveThread;
//    std::thread inputThread;
    std::thread ioThread;
    std::atomic<bool> running{false};
    std::atomic<bool> connected{false};
    std::mutex connectionMutex;
    
    std::function<void()> openCallback;
    std::function<void(int, const std::string&)> closeCallback;
    std::function<void(const std::string&)> errorCallback;
    
    void doConnect();

public:
    explicit MessageHandler(std::string uri, std::string  token = "");
    ~MessageHandler();
    
    static void connect(const std::string& uri, const std::string& token);
    void close();
    bool isOpen() const;
    bool isClosed() const;
    bool isClosing() const;
    
    static MessageHandler* getInstance() { return instance; }
    
    void onOpen(std::function<void()> callback);
    void onMessage(const std::vector<uint8_t>& message);
    void onClose(std::function<void(int, const std::string&)> callback);
    void onError(std::function<void(const std::string&)> callback);
    
    void send(const std::vector<uint8_t>& data);
    
    static bool isOpen(MessageHandler* handler) {
        return handler && handler->isOpen();
    }
};

} // namespace team::cool::client::websocket::handler

