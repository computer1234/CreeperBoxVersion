#include "util/crypt/HS256.h"
#include "util/crypt/Combiner.h"
#include <openssl/hmac.h>
#include <openssl/evp.h>
#include <sstream>
#include <iomanip>
#include <vector>
#include <cstring>

namespace team::cool::client::util::crypt {

std::string HS256::key;

std::string HS256::signS2C(const std::vector<uint8_t>& data) {
    std::string combinedKey = Combiner::doKeys(key, SECRET);
    
    unsigned char digest[EVP_MAX_MD_SIZE];
    unsigned int digestLen;
    
    HMAC(EVP_sha256(), combinedKey.data(), combinedKey.length(),
         data.data(), data.size(), digest, &digestLen);
    
    // 直接使用 digest 进行 Base64 编码
    const char base64_chars[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    std::string result;
    for (size_t i = 0; i < digestLen; i += 3) {
        uint32_t b = (digest[i] << 16) | ((i + 1 < digestLen ? digest[i + 1] : 0) << 8) | (i + 2 < digestLen ? digest[i + 2] : 0);
        result += base64_chars[(b >> 18) & 0x3F];
        result += base64_chars[(b >> 12) & 0x3F];
        result += (i + 1 < digestLen) ? base64_chars[(b >> 6) & 0x3F] : '=';
        result += (i + 2 < digestLen) ? base64_chars[b & 0x3F] : '=';
    }
    
    return result;
}

std::string HS256::signC2S(const std::vector<uint8_t>& data) {
    std::string combinedKey = Combiner::doKeys(SECRET, key);
    
    unsigned char digest[EVP_MAX_MD_SIZE];
    unsigned int digestLen;
    
    HMAC(EVP_sha256(), combinedKey.data(), combinedKey.length(),
         data.data(), data.size(), digest, &digestLen);
    
    // 直接使用 digest 进行 Base64 编码
    const char base64_chars[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    std::string result;
    for (size_t i = 0; i < digestLen; i += 3) {
        uint32_t b = (digest[i] << 16) | ((i + 1 < digestLen ? digest[i + 1] : 0) << 8) | (i + 2 < digestLen ? digest[i + 2] : 0);
        result += base64_chars[(b >> 18) & 0x3F];
        result += base64_chars[(b >> 12) & 0x3F];
        result += (i + 1 < digestLen) ? base64_chars[(b >> 6) & 0x3F] : '=';
        result += (i + 2 < digestLen) ? base64_chars[b & 0x3F] : '=';
    }
    
    return result;
}

bool HS256::verify(const std::vector<uint8_t>& data, const std::string& signature) {
    return signS2C(data) == signature;
}

} // namespace team::cool::client::util::crypt

