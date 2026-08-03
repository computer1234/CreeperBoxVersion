#include "util/Http.h"
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <openssl/bio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <sstream>
#include <stdexcept>
#include <cstring>
#include <regex>
#include "../../Utils/Logger.h"

namespace team::cool::client::util {

void* Http::client = nullptr;

void Http::init() {
    static bool initialized = false;
    if (!initialized) {
        SSL_library_init();
        SSL_load_error_strings();
        OpenSSL_add_all_algorithms();
        initialized = true;
    }
}

void Http::cleanup() {
    EVP_cleanup();
    ERR_free_strings();
}

// 解析 URL
static bool parseUrl(const std::string& url, std::string& host, std::string& path, int& port, bool& useSSL) {
    std::regex urlRegex(R"(^(https?)://([^:/]+)(?::(\d+))?(/.*)?)");
    std::smatch match;
    
    if (!std::regex_match(url, match, urlRegex)) {
        return false;
    }
    
    useSSL = (match[1].str() == "https");
    host = match[2].str();
    port = match[3].matched ? std::stoi(match[3].str()) : (useSSL ? 443 : 80);
    path = match[4].matched ? match[4].str() : "/";
    
    return true;
}

// 创建 socket 连接
static int createConnection(const std::string& host, int port) {
    struct hostent* server = gethostbyname(host.c_str());
    if (!server) {
        LOGE("Http: Failed to resolve host: %s", host.c_str());
        return -1;
    }
    
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        LOGE("Http: Failed to create socket");
        return -1;
    }
    
    // 设置超时
    struct timeval timeout;
    timeout.tv_sec = 10;
    timeout.tv_usec = 0;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
    setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout));
    
    struct sockaddr_in serverAddr;
    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port);
    memcpy(&serverAddr.sin_addr.s_addr, server->h_addr, server->h_length);
    
    if (connect(sock, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
        LOGE("Http: Failed to connect to %s:%d", host.c_str(), port);
        close(sock);
        return -1;
    }
    
    return sock;
}

std::string Http::post(const std::string& url, const std::string& cookie) {
    init();
    
    std::string host, path;
    int port;
    bool useSSL;
    
    if (!parseUrl(url, host, path, port, useSSL)) {
        throw std::runtime_error("Invalid URL: " + url);
    }
    
    LOGI("Http: POST %s (host=%s, port=%d, ssl=%d)", url.c_str(), host.c_str(), port, useSSL);
    
    int sock = createConnection(host, port);
    if (sock < 0) {
        throw std::runtime_error("Failed to connect");
    }
    
    std::string response;
    
    // 构建 HTTP 请求
    std::ostringstream request;
    request << "POST " << path << " HTTP/1.1\r\n";
    request << "Host: " << host << "\r\n";
    request << "Content-Type: application/x-www-form-urlencoded\r\n";
    request << "Content-Length: 0\r\n";
    if (!cookie.empty()) {
        request << "Cookie: " << cookie << "\r\n";
    }
    request << "Connection: close\r\n";
    request << "\r\n";
    
    std::string requestStr = request.str();
    
    if (useSSL) {
        // HTTPS 请求
        SSL_CTX* ctx = SSL_CTX_new(TLS_client_method());
        if (!ctx) {
            close(sock);
            throw std::runtime_error("Failed to create SSL context");
        }
        
        SSL* ssl = SSL_new(ctx);
        SSL_set_fd(ssl, sock);
        SSL_set_tlsext_host_name(ssl, host.c_str());  // SNI
        
        if (SSL_connect(ssl) <= 0) {
            LOGE("Http: SSL handshake failed");
            SSL_free(ssl);
            SSL_CTX_free(ctx);
            close(sock);
            throw std::runtime_error("SSL handshake failed");
        }
        
        // 发送请求
        SSL_write(ssl, requestStr.c_str(), requestStr.length());
        
        // 读取响应
        char buffer[4096];
        int bytesRead;
        while ((bytesRead = SSL_read(ssl, buffer, sizeof(buffer) - 1)) > 0) {
            buffer[bytesRead] = '\0';
            response += buffer;
        }
        
        SSL_shutdown(ssl);
        SSL_free(ssl);
        SSL_CTX_free(ctx);
    } else {
        // HTTP 请求
        send(sock, requestStr.c_str(), requestStr.length(), 0);
        
        char buffer[4096];
        int bytesRead;
        while ((bytesRead = recv(sock, buffer, sizeof(buffer) - 1, 0)) > 0) {
            buffer[bytesRead] = '\0';
            response += buffer;
        }
    }
    
    close(sock);
    
    // 解析响应 body (跳过 headers)
    size_t bodyStart = response.find("\r\n\r\n");
    if (bodyStart != std::string::npos) {
        response = response.substr(bodyStart + 4);
    }
    
    // 处理 chunked 编码 (简化处理)
    if (response.find("0\r\n\r\n") != std::string::npos) {
        // 如果是 chunked，尝试提取第一个 chunk
        size_t chunkEnd = response.find("\r\n");
        if (chunkEnd != std::string::npos) {
            size_t chunkSize = std::stoul(response.substr(0, chunkEnd), nullptr, 16);
            if (chunkSize > 0) {
                response = response.substr(chunkEnd + 2, chunkSize);
            }
        }
    }
    
    LOGI("Http: Response length: %zu", response.length());
    return response;
}

} // namespace team::cool::client::util
