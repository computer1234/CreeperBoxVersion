#include "websocket/packet/s2c/login/S2CPacketHandshake.h"
#include "util/crypt/ChaCha20.h"
#include "util/crypt/Combiner.h"
#include "util/crypt/ECDH.h"
#include "util/crypt/HKDF.h"
#include "util/crypt/SecureRandom.h"
#include "websocket/packet/c2s/login/C2SPacketPing.h"
#include "../../../../../Utils/Logger.h"
#include "Main.h"
#include "Config.h"
#include "Guard/RenderConfig.h"
#include "Guard/FrameCounter.h"
#include "Guard/EnvironmentScanner.h"
#include "../../../../../Memory/GameData.h"
#include <openssl/evp.h>
#include <stdexcept>
#include <memory>
#include <climits>
#include <openssl/x509.h>
#include <cstdlib>

namespace team::cool::client::websocket::packet::s2c::login {

// 计算字符串的 hashCode（与 Java String.hashCode() 完全兼容）
// Java: int hash = 0; for (char c : s) hash = 31 * hash + c;
static int64_t stringHashCode(const std::string& str) {
    int32_t hash = 0;  // Java int 是 32 位有符号
    for (unsigned char c : str) {
        // Java 的 char 是 16 位无符号，但 ASCII 字符串转换时用 unsigned char
        hash = 31 * hash + static_cast<int32_t>(c);
        // int32_t 自动处理溢出（与 Java int 行为一致）
    }
    // Math.abs 在 Java 中对 Integer.MIN_VALUE 返回负值，但我们假设哈希不会正好是那个值
    return static_cast<int64_t>(hash >= 0 ? hash : -hash);
}

void S2CPacketHandshake::read(ByteBuf& buf) {
    salt = buf.readString();
    int32_t len = buf.readInt();
    serverPublic = buf.readBytes(len);
    LOGI("[ECDH] 收到服务器握手包, salt长度: %zu, 公钥长度: %d bytes", salt.length(), len);
}

void S2CPacketHandshake::handle() {
    LOGI("[ECDH] 开始处理服务器公钥...");

    const unsigned char* p = serverPublic.data();
    EVP_PKEY* serverPubKey = d2i_PUBKEY(
            nullptr,
            &p,
            static_cast<long>(serverPublic.size())
    );

    if (!serverPubKey) {
        LOGE("[ECDH] 解析服务器公钥失败");
        throw std::runtime_error("Failed to create server public key from X.509 data");
    }
    LOGI("[ECDH] 服务器公钥解析成功");

    if (EVP_PKEY_id(serverPubKey) != EVP_PKEY_X25519) {
        LOGE("[ECDH] 服务器公钥类型错误, 不是 X25519");
        EVP_PKEY_free(serverPubKey);
        throw std::runtime_error("Server public key is not X25519");
    }
    LOGI("[ECDH] 服务器公钥类型验证通过 (X25519)");

    EVP_PKEY_CTX* ctx = EVP_PKEY_CTX_new(util::crypt::ECDH::clientPrivate, nullptr);
    if (!ctx) {
        LOGE("[ECDH] 创建密钥派生上下文失败");
        EVP_PKEY_free(serverPubKey);
        throw std::runtime_error("Failed to create ECDH context");
    }

    if (EVP_PKEY_derive_init(ctx) <= 0) {
        LOGE("[ECDH] 初始化密钥派生失败");
        EVP_PKEY_CTX_free(ctx);
        EVP_PKEY_free(serverPubKey);
        throw std::runtime_error("Failed to initialize ECDH");
    }

    if (EVP_PKEY_derive_set_peer(ctx, serverPubKey) <= 0) {
        LOGE("[ECDH] 设置对端公钥失败");
        EVP_PKEY_CTX_free(ctx);
        EVP_PKEY_free(serverPubKey);
        throw std::runtime_error("Failed to set peer");
    }
    LOGI("[ECDH] 对端公钥设置成功");

    size_t sharedLen = 0;
    if (EVP_PKEY_derive(ctx, nullptr, &sharedLen) <= 0) {
        LOGE("[ECDH] 获取共享密钥长度失败");
        EVP_PKEY_CTX_free(ctx);
        EVP_PKEY_free(serverPubKey);
        throw std::runtime_error("Failed to get shared secret length");
    }

    std::vector<uint8_t> shared(sharedLen);
    if (EVP_PKEY_derive(ctx, shared.data(), &sharedLen) <= 0) {
        LOGE("[ECDH] 派生共享密钥失败");
        EVP_PKEY_CTX_free(ctx);
        EVP_PKEY_free(serverPubKey);
        throw std::runtime_error("Failed to derive shared secret");
    }
    LOGI("[ECDH] 共享密钥派生成功, 长度: %zu bytes", sharedLen);

    EVP_PKEY_CTX_free(ctx);
    EVP_PKEY_free(serverPubKey);

    // 使用 licenseId (Config::getUsername()) 的 hashCode 计算密钥
    // 与 Backend C2SPacketHandshake.java 保持一致
    std::string licenseId = Config::getUsername();
    int64_t seed = stringHashCode(licenseId);
    int length = static_cast<int>((seed * 61) % 23) + 1;
    std::string key = util::crypt::Combiner::doKeys(salt, std::to_string(length * seed));

    LOGI("[ECDH] 使用卡密ID计算密钥, seed: %lld, length: %d", static_cast<long long>(seed), length);

    std::vector<uint8_t> keyBytes(key.begin(), key.end());
    util::crypt::ChaCha20::setC2s(util::crypt::HKDF::generate(shared, keyBytes, util::crypt::HKDF::getC2s(), 32));
    util::crypt::ChaCha20::setS2c(util::crypt::HKDF::generate(shared, keyBytes, util::crypt::HKDF::getS2c(), 32));
    LOGI("[ECDH] ChaCha20 加密密钥已设置 (C2S/S2C)");

    LOGI("[ECDH] ========== Key exchange complete ==========");

    // Distributed seed initialization (spread across multiple subsystems)
    // 确保temp_random_value在[1, INT_MAX/23)范围内，避免baseSeed=0
    int temp_random_value = util::crypt::SecureRandom::randomInt(INT_MAX / 23 - 1) + 1;
    uint32_t baseSeed = static_cast<uint32_t>(temp_random_value) * 23;  // 保证是23的非零倍数

    // Initialize render configuration (seed manager)
    guard::RenderConfig::updateTextureConfig(baseSeed);

    // Initialize memory layout configuration (seed D)
    // Must also be divisible by 23 for validation to pass
    uint32_t memorySeed = (baseSeed ^ 0xDEADBEEF);
    memorySeed = (memorySeed / 23) * 23;  // Round down to nearest multiple of 23
    if (memorySeed == 0) memorySeed = 23;  // Ensure non-zero
    GameData::initializeMemoryConfig(memorySeed);

    // Start frame counter session (time decay)
    guard::FrameCounter::startSession();

    // Initialize environment scanner
    guard::EnvironmentScanner::initializeScanner();

    // Update internal state (obfuscated)
    Main::m_renderState.store(static_cast<int>(baseSeed), std::memory_order_release);
    Main::setStatus(VerifyStatus::SUCCESS);

    LOGI("[ECDH] Session initialized, sending Ping...");

    auto pingPacket = std::make_shared<c2s::login::C2SPacketPing>("Ciallo world!");
    send(pingPacket);
}

} // namespace team::cool::client::websocket::packet::s2c::login
