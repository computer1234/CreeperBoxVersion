#pragma once

#include <vector>
#include <cstdint>
#include <string>

namespace team::cool::client::util {

class Generator {
public:
    static std::vector<uint8_t> randomBytes(size_t length);
    static std::string randomString(size_t length);  // 生成指定长度的随机字符串
    static std::string nonce();
};

} // namespace team::cool::client::util

