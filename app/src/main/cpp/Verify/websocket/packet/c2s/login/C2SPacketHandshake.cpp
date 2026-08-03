#include "websocket/packet/c2s/login/C2SPacketHandshake.h"
#include "util/crypt/ECDH.h"
#include "../../../../../Utils/Logger.h"
#include <openssl/evp.h>
#include <stdexcept>
#include <openssl/x509.h>

namespace team::cool::client::websocket::packet::c2s::login {

void C2SPacketHandshake::write(ByteBuf& buf) {
    buf.writeInt(static_cast<int32_t>(clientPublic.size()));
    buf.writeBytes(clientPublic);
}

void C2SPacketHandshake::handle() {
    LOGI("[ECDH] 开始生成 X25519 密钥对...");

    EVP_PKEY_CTX* ctx = EVP_PKEY_CTX_new_id(EVP_PKEY_X25519, nullptr);
    if (!ctx) {
        LOGE("[ECDH] 创建 X25519 上下文失败");
        throw std::runtime_error("Failed to create X25519 context");
    }

    EVP_PKEY* pkey = nullptr;
    if (EVP_PKEY_keygen_init(ctx) <= 0 || EVP_PKEY_keygen(ctx, &pkey) <= 0) {
        LOGE("[ECDH] 生成密钥对失败");
        EVP_PKEY_CTX_free(ctx);
        throw std::runtime_error("Failed to generate key pair");
    }

    util::crypt::ECDH::clientPrivate = pkey;
    LOGI("[ECDH] 客户端私钥已生成");

    int pubLen = i2d_PUBKEY(pkey, nullptr);
    if (pubLen <= 0) {
        LOGE("[ECDH] 获取公钥长度失败");
        EVP_PKEY_CTX_free(ctx);
        EVP_PKEY_free(pkey);
        throw std::runtime_error("Failed to get X509 public key size");
    }

    clientPublic.resize(pubLen);
    unsigned char* p = clientPublic.data();
    if (i2d_PUBKEY(pkey, &p) != pubLen) {
        LOGE("[ECDH] 导出公钥失败");
        EVP_PKEY_CTX_free(ctx);
        EVP_PKEY_free(pkey);
        throw std::runtime_error("Failed to get X509 public key");
    }

    LOGI("[ECDH] 客户端公钥已生成, 长度: %d bytes", pubLen);
    EVP_PKEY_CTX_free(ctx);
}

} // namespace team::cool::client::websocket::packet::c2s::login

