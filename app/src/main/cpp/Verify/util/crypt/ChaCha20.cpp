#include "util/crypt/ChaCha20.h"
#include <openssl/evp.h>
#include <stdexcept>

namespace team::cool::client::util::crypt {

std::vector<uint8_t> ChaCha20::c2s;
std::vector<uint8_t> ChaCha20::s2c;

std::vector<uint8_t> ChaCha20::encrypt(const std::vector<uint8_t>& nonce, const std::vector<uint8_t>& plaintext) {
    if (c2s.empty()) return {};

    EVP_CIPHER_CTX* ctx = EVP_CIPHER_CTX_new();
    if (!ctx) throw std::runtime_error("Failed to create cipher context");

    if (EVP_EncryptInit_ex(ctx, EVP_chacha20_poly1305(), nullptr, c2s.data(), nonce.data()) != 1) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Failed to initialize encryption");
    }

    std::vector<uint8_t> ciphertext(plaintext.size() + 16);
    int len;
    int ciphertextLen = 0;

    if (EVP_EncryptUpdate(ctx, ciphertext.data(), &len, plaintext.data(), plaintext.size()) != 1) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Encryption failed");
    }
    ciphertextLen += len;

    if (EVP_EncryptFinal_ex(ctx, ciphertext.data() + ciphertextLen, &len) != 1) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Encryption finalization failed");
    }
    ciphertextLen += len;

    const int tag_length = 16;
    if (EVP_CIPHER_CTX_ctrl(ctx, EVP_CTRL_AEAD_GET_TAG, tag_length, ciphertext.data() + ciphertextLen) != 1) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Failed to get Poly1305 tag");
    }

    ciphertextLen += tag_length;

    EVP_CIPHER_CTX_free(ctx);
    ciphertext.resize(ciphertextLen);
    return ciphertext;
}

std::vector<uint8_t> ChaCha20::decrypt(const std::vector<uint8_t>& nonce, const std::vector<uint8_t>& ciphertext) {
    if (s2c.empty() || ciphertext.size() < 16) return {};

    EVP_CIPHER_CTX* ctx = EVP_CIPHER_CTX_new();
    if (!ctx) throw std::runtime_error("Failed to create cipher context");

    const int tag_length = 16;
    const size_t encrypted_data_len = ciphertext.size() - tag_length;

    if (EVP_DecryptInit_ex(ctx, EVP_chacha20_poly1305(), nullptr, s2c.data(), nonce.data()) != 1) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Failed to initialize decryption");
    }

    const uint8_t* tag_data = ciphertext.data() + encrypted_data_len;
    if (EVP_CIPHER_CTX_ctrl(ctx, EVP_CTRL_AEAD_SET_TAG, tag_length, (void*)tag_data) != 1) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Failed to set Poly1305 tag for verification");
    }

    std::vector<uint8_t> plaintext(encrypted_data_len);
    int len;
    int plaintextLen = 0;

    if (EVP_DecryptUpdate(ctx, plaintext.data(), &len, ciphertext.data(), encrypted_data_len) != 1) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Decryption update failed");
    }
    plaintextLen += len;

    if (EVP_DecryptFinal_ex(ctx, plaintext.data() + plaintextLen, &len) <= 0) {
        EVP_CIPHER_CTX_free(ctx);
        throw std::runtime_error("Decryption finalization failed: Bad Tag or Corrupt Data");
    }
    plaintextLen += len;

    EVP_CIPHER_CTX_free(ctx);
    plaintext.resize(plaintextLen);
    return plaintext;
}

} // namespace team::cool::client::util::crypt

