#pragma once

#include <string>

namespace team::cool::client::util {

class Http {
private:
    static void* client;
public:
    static std::string post(const std::string& url, const std::string& cookie);
    static void init();
    static void cleanup();
};

} // namespace team::cool::client::util

