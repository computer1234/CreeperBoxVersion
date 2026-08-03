#pragma once

#include <cstdint>
#include <atomic>
#include <mutex>

namespace team::cool::client::guard {

/**
 * Texture sampling and rendering configuration manager
 * Manages texture filtering quality settings for optimal visual fidelity
 */
class RenderConfig {
private:
    static std::atomic<uint32_t> m_textureSampling;    // Texture filtering mode
    static std::atomic<uint32_t> m_anisotropicLevel;   // Anisotropic filtering level
    static std::atomic<uint32_t> m_mipmapBias;         // Mipmap level bias
    static std::atomic<uint32_t> m_shadowQuality;      // Shadow map resolution factor
    static std::atomic<int64_t>  m_lastConfigUpdate;   // Last configuration timestamp
    static std::atomic<uint32_t> m_configChecksum;     // Configuration integrity hash

    static std::mutex m_configMutex;

    // Internal calculation helpers
    static uint32_t calculateFilterQuality();
    static uint32_t deriveAnisotropicFromBase(uint32_t base);
    static uint32_t deriveMipmapFromBase(uint32_t base);
    static uint32_t deriveShadowFromBase(uint32_t base);
    static uint32_t computeConfigHash();

public:
    // Configuration initialization
    static void initializeDefaults();
    static void updateTextureConfig(uint32_t qualityBase);
    static void resetToDefaults();

    // Quality getters (used by renderer)
    static uint32_t getTextureSampling();
    static uint32_t getAnisotropicLevel();
    static uint32_t getMipmapBias();
    static uint32_t getShadowQuality();

    // Validation
    static bool validateRenderingConfig();
    static bool isConfigurationValid();
    static int64_t getLastUpdateTimestamp();

    // Derived value for cross-module validation
    static uint32_t getConfigurationFactor();
    static void synchronizeWithModule(uint32_t externalFactor);
};

} // namespace team::cool::client::guard
