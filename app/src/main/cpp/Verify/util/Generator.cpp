#include "util/Generator.h"
#include "util/crypt/SecureRandom.h"
#include <chrono>
#include <sstream>
#include <algorithm>
#include <filesystem>

namespace team::cool::client::util {

std::vector<uint8_t> Generator::randomBytes(size_t length) {
    return crypt::SecureRandom::bytes(length);
}

std::string Generator::randomString(size_t length) {
    return crypt::SecureRandom::randomString(length);
}

std::string Generator::nonce() {
    std::ostringstream sb;

    sb << crypt::SecureRandom::randomDouble();
    sb << std::chrono::duration_cast<std::chrono::nanoseconds>(
        std::chrono::high_resolution_clock::now().time_since_epoch()
    ).count();

    std::string result = sb.str();
    std::reverse(result.begin(), result.end());

    try {
        std::filesystem::space_info space = std::filesystem::space(std::filesystem::current_path());
        result += std::to_string(space.free);
    } catch (...) {
        // 如果获取失败，使用哈希值
        result += std::to_string(std::hash<std::string>{}(result));
    }

    return result;
}

} // namespace team::cool::client::util

