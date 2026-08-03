#pragma once

#include <vector>
#include <cstdint>
#include <string>

namespace team::cool::client::util::crypt {

class SecureRandom {
public:
    // 生成密码学安全的随机字节
    static std::vector<uint8_t> bytes(size_t length);

    // 生成密码学安全的随机整数 [0, max)
    static int randomInt(int max);

    // 生成密码学安全的随机整数 [min, max]
    static int randomIntRange(int min, int max);

    // 生成密码学安全的随机 double [0.0, 1.0)
    static double randomDouble();

    // 生成密码学安全的随机字符串
    static std::string randomString(size_t length);
};

} // namespace team::cool::client::util::crypt
