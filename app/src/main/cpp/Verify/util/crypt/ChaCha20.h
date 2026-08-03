#pragma once

#include <vector>
#include <cstdint>

namespace team::cool::client::util::crypt {

class ChaCha20 {
private:
    static std::vector<uint8_t> c2s;
    static std::vector<uint8_t> s2c;

public:
    static void setC2s(const std::vector<uint8_t>& key) { c2s = key; }
    static void setS2c(const std::vector<uint8_t>& key) { s2c = key; }
    
    static std::vector<uint8_t> encrypt(const std::vector<uint8_t>& nonce, const std::vector<uint8_t>& plaintext);
    static std::vector<uint8_t> decrypt(const std::vector<uint8_t>& nonce, const std::vector<uint8_t>& ciphertext);
};

} // namespace team::cool::client::util::crypt

