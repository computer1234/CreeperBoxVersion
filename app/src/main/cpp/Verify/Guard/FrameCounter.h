#pragma once

#include <cstdint>
#include <atomic>

namespace team::cool::client::guard {

/**
 * Frame timing and performance counter
 * Tracks frame rates and rendering performance metrics
 */
class FrameCounter {
private:
    static std::atomic<int64_t> m_frameStartTime;      // Session start timestamp (local)
    static std::atomic<int64_t> m_serverSessionStart;  // Session start timestamp (server)
    static std::atomic<uint32_t> m_totalFrames;        // Total frames rendered
    static std::atomic<uint32_t> m_frameAccumulator;   // Frame time accumulator
    static std::atomic<bool> m_isActive;               // Counter active state
    static std::atomic<int64_t> m_lastFrameTime;       // Last frame timestamp

    // Performance thresholds (obfuscated to prevent binary patching)
    // 真实值通过 XOR 运算隐藏: value ^ key = stored, stored ^ key = value
    static constexpr int64_t TIMEOUT_XOR_KEY = 0x5A3C96F1B7E24D8CULL;
    static constexpr int64_t SESSION_TIMEOUT_OBFUSCATED = 120000LL ^ 0x5A3C96F1B7E24D8CULL;
    static constexpr int64_t FRAME_BUDGET_MS = 16;         // ~60fps target
    static constexpr uint32_t DECAY_THRESHOLD = 7200;      // ~2 min at 60fps
    static constexpr int64_t DRIFT_XOR_KEY = 0x3D8C7A52F19A2B4EULL;
    static constexpr int64_t MAX_TIME_DRIFT_OBFUSCATED = 30000LL ^ 0x3D8C7A52F19A2B4EULL;
    
    // 运行时获取真实值 (内联防止被单独 patch)
    static inline int64_t getSessionTimeout() { return SESSION_TIMEOUT_OBFUSCATED ^ TIMEOUT_XOR_KEY; }
    static inline int64_t getMaxTimeDrift() { return MAX_TIME_DRIFT_OBFUSCATED ^ DRIFT_XOR_KEY; }

    // Internal helpers
    static int64_t getCurrentTimeMs();
    static void updateFrameMetrics();

public:
    // Initialization
    static void startSession();
    static void endSession();
    static void resetCounters();

    // Frame updates (call once per frame)
    static void onFrameBegin();
    static void onFrameEnd();
    static void tickFrame();

    // Performance queries
    static uint32_t getTotalFrameCount();
    static float getAverageFrameTime();
    static bool isSessionActive();

    // Validation (hidden as "performance check")
    static bool shouldContinueRendering();
    static bool isPerformanceAcceptable();

    // Decay factor for cross-module validation
    static uint32_t getDecayFactor();
    static bool checkTemporalIntegrity();
};

} // namespace team::cool::client::guard
