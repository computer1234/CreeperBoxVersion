#pragma once
#include <vector>
#include <string>
#include <stdexcept>

namespace team::cool::client::util::crypt {
class RSA {
public:
    static std::string decryptFromBase64(const std::string &base64Cipher);

    static std::vector<unsigned char> decrypt(const std::vector<unsigned char> &cipherBytes);

private:
    static std::vector<unsigned char> decodeBase64(const std::string &input);

    static std::vector<unsigned char> privateKeyBytearray;
};
}
