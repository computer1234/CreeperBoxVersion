#include "RenderConfig.h"
#include "../MacroFix.h"
#include "../util/DateTimeUtil.h"
#include "../../Utils/Logger.h"
#include <chrono>

namespace team::cool::client::guard {

// Static member definitions
std::atomic<uint32_t> RenderConfig::m_textureSampling{1};
std::atomic<uint32_t> RenderConfig::m_anisotropicLevel{1};
std::atomic<uint32_t> RenderConfig::m_mipmapBias{1};
std::atomic<uint32_t> RenderConfig::m_shadowQuality{1};
std::atomic<int64_t>  RenderConfig::m_lastConfigUpdate{0};
std::atomic<uint32_t> RenderConfig::m_configChecksum{0};
std::mutex RenderConfig::m_configMutex;

// Magic constants for seed derivation (obfuscated)
namespace {
    constexpr uint32_t FILTER_PRIME = 0x9E3779B1;
    constexpr uint32_t ANISO_SHIFT = 7;
    constexpr uint32_t MIPMAP_MASK = 0xFFFF;
    constexpr uint32_t SHADOW_XOR = 0xDEADC0DE;
    constexpr uint32_t VALIDATION_MOD = 17;
    constexpr uint32_t CROSS_CHECK_MOD = 23;
    
    // XOR混淆密钥 - 每个种子使用不同的密钥防止模式识别
    constexpr uint32_t OBFUSCATE_KEY_A = 0x5A3C96F1;
    constexpr uint32_t OBFUSCATE_KEY_B = 0xB7E24D8C;
    constexpr uint32_t OBFUSCATE_KEY_C = 0xF19A2B4E;
    constexpr uint32_t OBFUSCATE_KEY_D = 0x3D8C7A52;
    constexpr uint32_t OBFUSCATE_KEY_HASH = 0x8E1F3C7B;
    
    // 辅助函数：混淆存储
    inline uint32_t obfuscateStore(uint32_t value, uint32_t key) {
        return value ^ key;
    }
    
    // 辅助函数：解混淆读取
    inline uint32_t obfuscateLoad(uint32_t stored, uint32_t key) {
        return stored ^ key;
    }
}

uint32_t RenderConfig::calculateFilterQuality() {
    uint32_t a = obfuscateLoad(m_textureSampling.load(std::memory_order_acquire), OBFUSCATE_KEY_A);
    uint32_t b = obfuscateLoad(m_anisotropicLevel.load(std::memory_order_acquire), OBFUSCATE_KEY_B);
    return ((a ^ b) * FILTER_PRIME) >> 16;
}

uint32_t RenderConfig::deriveAnisotropicFromBase(uint32_t base) {
    // Derive second seed from base using non-linear transformation
    uint32_t temp = base ^ FILTER_PRIME;
    temp = ((temp >> ANISO_SHIFT) | (temp << (32 - ANISO_SHIFT)));
    return temp * 0x85EBCA77;
}

uint32_t RenderConfig::deriveMipmapFromBase(uint32_t base) {
    // Third seed derivation - must work with shadow to satisfy validation formula
    uint32_t temp = base & MIPMAP_MASK;
    temp = temp * temp;
    return (temp ^ (base >> 16)) | 1;  // Ensure non-zero
}

uint32_t RenderConfig::deriveShadowFromBase(uint32_t base) {
    // Fourth seed derivation - simplified version without recursive calls
    // The validation formula check is handled separately in updateTextureConfig
    return (base ^ SHADOW_XOR) + ((base >> 8) & 0xFF00FF);
}

uint32_t RenderConfig::computeConfigHash() {
    uint32_t a = obfuscateLoad(m_textureSampling.load(std::memory_order_acquire), OBFUSCATE_KEY_A);
    uint32_t b = obfuscateLoad(m_anisotropicLevel.load(std::memory_order_acquire), OBFUSCATE_KEY_B);
    uint32_t c = obfuscateLoad(m_mipmapBias.load(std::memory_order_acquire), OBFUSCATE_KEY_C);
    uint32_t d = obfuscateLoad(m_shadowQuality.load(std::memory_order_acquire), OBFUSCATE_KEY_D);

    // Simple hash combining all seeds
    return ((a * 31 + b) * 31 + c) * 31 + d;
}

void RenderConfig::initializeDefaults() {
    std::lock_guard<std::mutex> lock(m_configMutex);
    // 存储时使用XOR混淆
    m_textureSampling.store(obfuscateStore(1, OBFUSCATE_KEY_A), std::memory_order_release);
    m_anisotropicLevel.store(obfuscateStore(1, OBFUSCATE_KEY_B), std::memory_order_release);
    m_mipmapBias.store(obfuscateStore(1, OBFUSCATE_KEY_C), std::memory_order_release);
    m_shadowQuality.store(obfuscateStore(1, OBFUSCATE_KEY_D), std::memory_order_release);
    m_lastConfigUpdate.store(0, std::memory_order_release);
    m_configChecksum.store(obfuscateStore(0, OBFUSCATE_KEY_HASH), std::memory_order_release);
}

void RenderConfig::updateTextureConfig(uint32_t qualityBase) {
    std::lock_guard<std::mutex> lock(m_configMutex);

    LOGI("[SEED] 初始化种子: base=0x%08X", qualityBase);

    // Distribute the base seed across all configuration values (使用XOR混淆存储)
    m_textureSampling.store(obfuscateStore(qualityBase, OBFUSCATE_KEY_A), std::memory_order_release);
    uint32_t anisoVal = deriveAnisotropicFromBase(qualityBase);
    uint32_t mipmapVal = deriveMipmapFromBase(qualityBase);
    uint32_t shadowVal = deriveShadowFromBase(qualityBase);

    // Fix validation formula: ensure (a ^ b) % 17 == (c * d) % 17
    // Calculate target value and adjust shadow to satisfy the formula
    uint32_t leftSide = (qualityBase ^ anisoVal) % VALIDATION_MOD;
    uint32_t cMod = mipmapVal % VALIDATION_MOD;

    // Find d such that (cMod * d) % 17 == leftSide
    // We need to find d where (cMod * d) % 17 == leftSide
    // Try values 0-16 to find matching d modulo
    uint32_t targetDMod = 0;
    for (uint32_t i = 0; i < VALIDATION_MOD; i++) {
        if ((cMod * i) % VALIDATION_MOD == leftSide) {
            targetDMod = i;
            break;
        }
    }

    // Adjust shadowVal to have the correct modulo while preserving high bits
    uint32_t currentDMod = shadowVal % VALIDATION_MOD;
    if (currentDMod != targetDMod) {
        // Adjust shadow value: subtract current mod, add target mod
        shadowVal = (shadowVal - currentDMod) + targetDMod;
    }

    LOGD("[SEED] 公式调整: leftSide=%u, cMod=%u, targetDMod=%u", leftSide, cMod, targetDMod);

    // 使用XOR混淆存储种子
    m_anisotropicLevel.store(obfuscateStore(anisoVal, OBFUSCATE_KEY_B), std::memory_order_release);
    m_mipmapBias.store(obfuscateStore(mipmapVal, OBFUSCATE_KEY_C), std::memory_order_release);
    m_shadowQuality.store(obfuscateStore(shadowVal, OBFUSCATE_KEY_D), std::memory_order_release);

    LOGI("[SEED] 派生种子A(texture)=0x%08X", qualityBase);
    LOGI("[SEED] 派生种子B(aniso)=0x%08X", anisoVal);
    LOGI("[SEED] 派生种子C(mipmap)=0x%08X", mipmapVal);
    LOGI("[SEED] 派生种子D(shadow)=0x%08X", shadowVal);

    // Record update timestamp
    m_lastConfigUpdate.store(util::DateTimeUtil::systemTime(), std::memory_order_release);

    // Compute integrity checksum
    uint32_t checksum = computeConfigHash();
    m_configChecksum.store(obfuscateStore(checksum, OBFUSCATE_KEY_HASH), std::memory_order_release);

    LOGI("[SEED] 配置校验和=0x%08X", checksum);
}

void RenderConfig::resetToDefaults() {
    initializeDefaults();
}

uint32_t RenderConfig::getTextureSampling() {
    return obfuscateLoad(m_textureSampling.load(std::memory_order_acquire), OBFUSCATE_KEY_A);
}

uint32_t RenderConfig::getAnisotropicLevel() {
    return obfuscateLoad(m_anisotropicLevel.load(std::memory_order_acquire), OBFUSCATE_KEY_B);
}

uint32_t RenderConfig::getMipmapBias() {
    return obfuscateLoad(m_mipmapBias.load(std::memory_order_acquire), OBFUSCATE_KEY_C);
}

uint32_t RenderConfig::getShadowQuality() {
    return obfuscateLoad(m_shadowQuality.load(std::memory_order_acquire), OBFUSCATE_KEY_D);
}

bool RenderConfig::validateRenderingConfig() {
    // 读取并解混淆种子值
    uint32_t a = obfuscateLoad(m_textureSampling.load(std::memory_order_acquire), OBFUSCATE_KEY_A);
    uint32_t b = obfuscateLoad(m_anisotropicLevel.load(std::memory_order_acquire), OBFUSCATE_KEY_B);
    uint32_t c = obfuscateLoad(m_mipmapBias.load(std::memory_order_acquire), OBFUSCATE_KEY_C);
    uint32_t d = obfuscateLoad(m_shadowQuality.load(std::memory_order_acquire), OBFUSCATE_KEY_D);

    // Cross-validation formula (distributed check)
    // Seeds must satisfy: (a ^ b) % 17 == (c * d) % 17
    uint32_t left = (a ^ b) % VALIDATION_MOD;
    uint32_t right = ((c % VALIDATION_MOD) * (d % VALIDATION_MOD)) % VALIDATION_MOD;

    if (left != right) {
        LOGW("[SEED] FAIL: 公式不匹配 (a^b)%%17=%u, (c*d)%%17=%u, a=0x%08X, b=0x%08X, c=0x%08X, d=0x%08X",
             left, right, a, b, c, d);
        return false;
    }

    // Additional check: base seed validation
    if (a % CROSS_CHECK_MOD != 0) {
        LOGW("[SEED] FAIL: 模验证失败 a%%23=%u, a=0x%08X", a % CROSS_CHECK_MOD, a);
        return false;
    }

    // Verify checksum integrity
    uint32_t computedHash = computeConfigHash();
    uint32_t storedHash = obfuscateLoad(m_configChecksum.load(std::memory_order_acquire), OBFUSCATE_KEY_HASH);

    if (computedHash != storedHash) {
        LOGW("[SEED] FAIL: 校验和不匹配 计算=0x%08X, 存储=0x%08X", computedHash, storedHash);
        return false;
    }

    return true;
}

bool RenderConfig::isConfigurationValid() {
    return validateRenderingConfig();
}

int64_t RenderConfig::getLastUpdateTimestamp() {
    return m_lastConfigUpdate.load(std::memory_order_acquire);
}

uint32_t RenderConfig::getConfigurationFactor() {
    // Return a derived value for cross-module synchronization
    uint32_t a = obfuscateLoad(m_textureSampling.load(std::memory_order_acquire), OBFUSCATE_KEY_A);
    uint32_t d = obfuscateLoad(m_shadowQuality.load(std::memory_order_acquire), OBFUSCATE_KEY_D);
    return (a ^ d) & 0xFFFF;
}

void RenderConfig::synchronizeWithModule(uint32_t externalFactor) {
    // Allows other modules to verify they have consistent state
    uint32_t expected = getConfigurationFactor();
    if (externalFactor != expected) {
        // Desynchronization detected - could trigger additional checks
        // For now, just update our checksum to invalidate
        m_configChecksum.store(obfuscateStore(0, OBFUSCATE_KEY_HASH), std::memory_order_release);
    }
}

} // namespace team::cool::client::guard
