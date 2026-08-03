#include "util/crypt/SecureRandom.h"
#include <openssl/rand.h>
#include <stdexcept>
#include <cstring>

namespace team::cool::client::util::crypt {

std::vector<uint8_t> SecureRandom::bytes(size_t length) {
    std::vector<uint8_t> buffer(length);
    if (RAND_bytes(buffer.data(), static_cast<int>(length)) != 1) {
        throw std::runtime_error("Failed to generate secure random bytes");
    }
    return buffer;
}

int SecureRandom::randomInt(int max) {
    if (max <= 0) return 0;

    uint32_t value;
    if (RAND_bytes(reinterpret_cast<unsigned char*>(&value), sizeof(value)) != 1) {
        throw std::runtime_error("Failed to generate secure random int");
    }
    return static_cast<int>(value % static_cast<uint32_t>(max));
}

int SecureRandom::randomIntRange(int min, int max) {
    if (min >= max) return min;
    return min + randomInt(max - min + 1);
}

double SecureRandom::randomDouble() {
    uint64_t value;
    if (RAND_bytes(reinterpret_cast<unsigned char*>(&value), sizeof(value)) != 1) {
        throw std::runtime_error("Failed to generate secure random double");
    }
    // 使用 53 位精度 (double 的尾数位数)
    return (value >> 11) * (1.0 / 9007199254740992.0);
}

std::string SecureRandom::randomString(size_t length) {
    static const char charset[] =
        "abcdefghijklmnopqrstuvwxyz"
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "0123456789";
    static const size_t charsetSize = sizeof(charset) - 1;

    std::string result;
    result.reserve(length);

    auto randomBytes = bytes(length);
    for (size_t i = 0; i < length; ++i) {
        result += charset[randomBytes[i] % charsetSize];
    }
    return result;
}

} // namespace team::cool::client::util::crypt
