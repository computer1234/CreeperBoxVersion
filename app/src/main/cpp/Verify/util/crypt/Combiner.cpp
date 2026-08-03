#include "util/crypt/Combiner.h"
#include <openssl/evp.h>
#include <algorithm>
#include <sstream>
#include <iomanip>
#include <vector>

namespace team::cool::client::util::crypt {

    // 模拟 Java byte 的行为：
    // Java 中 byte << n 会先将 byte 提升为 int（符号扩展），然后位移
    // n 对 32 取模（int 是 32 位）
    __attribute__((noinline)) uint8_t rotateLeft(uint8_t b, int n) {
        // 模拟 Java 的符号扩展：将 uint8_t 转为有符号 int8_t 再转为 int
        int val = static_cast<int>(static_cast<int8_t>(b));
        // Java 位移 n 会对 32 取模
        int left = val << (n & 31);
        int right = (b & 0xFF) >> (8 - n);  // 无符号右移
        return static_cast<uint8_t>((left | right) & 0xFF);
    }

    __attribute__((noinline)) uint8_t rotateRight(uint8_t b, int n) {
        int val = static_cast<int>(static_cast<int8_t>(b));
        int right = (b & 0xFF) >> n;  // 无符号右移 (Java >>>)
        int left = val << ((8 - n) & 31);
        return static_cast<uint8_t>((right | left) & 0xFF);
    }

    std::string Combiner::doKeys(const std::string& key1, const std::string& key2) {
        const uint8_t* a = reinterpret_cast<const uint8_t*>(key1.data());
        const uint8_t* b = reinterpret_cast<const uint8_t*>(key2.data());
        size_t aLen = key1.length();
        size_t bLen = key2.length();

        std::vector<uint8_t> mixed;
        mixed.reserve(aLen + bLen);

        size_t maxLen = std::max(aLen, bLen);
        for (size_t i = 0; i < maxLen; ++i) {
            if (i < aLen) mixed.push_back(a[i]);
            if (i < bLen) mixed.push_back(b[i]);
        }

        for (size_t i = 0; i < mixed.size(); ++i) {
            if (i % 2 == 0) {
                mixed[i] = rotateLeft(mixed[i], (i % 23) + 1);
            } else {
                mixed[i] = rotateRight(mixed[i], (i % 5) + 1);
            }
        }

        // 使用 EVP 计算 SHA256
        unsigned char hash[EVP_MAX_MD_SIZE];
        unsigned int hashLen = 0;

        EVP_MD_CTX* ctx = EVP_MD_CTX_new();
        EVP_DigestInit_ex(ctx, EVP_sha256(), nullptr);
        EVP_DigestUpdate(ctx, mixed.data(), mixed.size());
        EVP_DigestFinal_ex(ctx, hash, &hashLen);
        EVP_MD_CTX_free(ctx);

        // Base64 encode
        const char base64_chars[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        std::string result;
        for (size_t i = 0; i < hashLen; i += 3) {
            uint32_t b = (hash[i] << 16) | ((i + 1 < hashLen ? hash[i + 1] : 0) << 8) | (i + 2 < hashLen ? hash[i + 2] : 0);
            result += base64_chars[(b >> 18) & 0x3F];
            result += base64_chars[(b >> 12) & 0x3F];
            result += (i + 1 < hashLen) ? base64_chars[(b >> 6) & 0x3F] : '=';
            result += (i + 2 < hashLen) ? base64_chars[b & 0x3F] : '=';
        }

        return result;
    }

} // namespace team::cool::client::util::crypt
