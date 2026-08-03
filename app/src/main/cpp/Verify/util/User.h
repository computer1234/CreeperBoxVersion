#pragma once

#include <string>
#include <chrono>
#include <ctime>

namespace team::cool::client::util {

class User {
private:
    static int64_t userId;
    static std::string username;
    static bool beta;
    static std::chrono::system_clock::time_point expiryTime;

public:
    static int64_t getUserId() { return userId; }
    static void setUserId(int64_t id) { userId = id; }
    
    static const std::string& getUsername() { return username; }
    static void setUsername(const std::string& name) { username = name; }
    
    static bool isBeta() { return beta; }
    static void setBeta(bool b) { beta = b; }
    
    static std::chrono::system_clock::time_point getExpiryTime() { return expiryTime; }
    static void setExpiryTime(const std::chrono::system_clock::time_point& time) { expiryTime = time; }
};

} // namespace team::cool::client::util

