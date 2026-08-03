#include "FrameCounter.h"
#include "../MacroFix.h"
#include "RenderConfig.h"
#include "../../Utils/Logger.h"
#include "../util/DateTimeUtil.h"
#include "../websocket/packet/s2c/S2CPacketKeepalive.h"
#include <chrono>

namespace team::cool::client::guard {

// Static member definitions
std::atomic<int64_t> FrameCounter::m_frameStartTime{0};
std::atomic<int64_t> FrameCounter::m_serverSessionStart{0};  // 服务器时间基准
std::atomic<uint32_t> FrameCounter::m_totalFrames{0};
std::atomic<uint32_t> FrameCounter::m_frameAccumulator{0};
std::atomic<bool> FrameCounter::m_isActive{false};
std::atomic<int64_t> FrameCounter::m_lastFrameTime{0};

int64_t FrameCounter::getCurrentTimeMs() {
    auto now = std::chrono::steady_clock::now();
    auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(
        now.time_since_epoch()
    ).count();
    return ms;
}

void FrameCounter::updateFrameMetrics() {
    int64_t currentTime = getCurrentTimeMs();
    int64_t lastTime = m_lastFrameTime.load(std::memory_order_acquire);

    if (lastTime > 0) {
        int64_t delta = currentTime - lastTime;
        // Accumulate frame time for averaging
        m_frameAccumulator.fetch_add(static_cast<uint32_t>(delta), std::memory_order_relaxed);
    }

    m_lastFrameTime.store(currentTime, std::memory_order_release);
    m_totalFrames.fetch_add(1, std::memory_order_relaxed);
}

void FrameCounter::startSession() {
    int64_t localTime = getCurrentTimeMs();
    int64_t serverTime = util::DateTimeUtil::systemTime();  // 用于与心跳lastActive比较
    
    m_frameStartTime.store(localTime, std::memory_order_release);
    m_serverSessionStart.store(serverTime, std::memory_order_release);
    m_totalFrames.store(0, std::memory_order_release);
    m_frameAccumulator.store(0, std::memory_order_release);
    m_lastFrameTime.store(0, std::memory_order_release);
    m_isActive.store(true, std::memory_order_release);

    LOGI("[TIME] 会话启动: 本地时间=%lld, 服务器时间基准=%lld", 
         static_cast<long long>(localTime), static_cast<long long>(serverTime));
}

void FrameCounter::endSession() {
    m_isActive.store(false, std::memory_order_release);
}

void FrameCounter::resetCounters() {
    m_totalFrames.store(0, std::memory_order_release);
    m_frameAccumulator.store(0, std::memory_order_release);
    m_lastFrameTime.store(0, std::memory_order_release);
}

void FrameCounter::onFrameBegin() {
    // Lightweight frame start marker
}

void FrameCounter::onFrameEnd() {
    updateFrameMetrics();
}

void FrameCounter::tickFrame() {
    // Unified tick function combining begin/end
    if (!m_isActive.load(std::memory_order_acquire)) {
        return;
    }
    updateFrameMetrics();
}

uint32_t FrameCounter::getTotalFrameCount() {
    return m_totalFrames.load(std::memory_order_acquire);
}

float FrameCounter::getAverageFrameTime() {
    uint32_t frames = m_totalFrames.load(std::memory_order_acquire);
    uint32_t accumulator = m_frameAccumulator.load(std::memory_order_acquire);

    if (frames == 0) return 0.0f;
    return static_cast<float>(accumulator) / static_cast<float>(frames);
}

bool FrameCounter::isSessionActive() {
    return m_isActive.load(std::memory_order_acquire);
}

bool FrameCounter::shouldContinueRendering() {
    // Primary time decay check
    bool isActive = m_isActive.load(std::memory_order_acquire);
    if (!isActive) {
        LOGW("[TIME] FAIL: 会话未激活");
        return false;
    }

    int64_t startTime = m_frameStartTime.load(std::memory_order_acquire);
    if (startTime == 0) {
        LOGW("[TIME] FAIL: 会话从未启动");
        return false;  // Session never started
    }

    int64_t currentTime = getCurrentTimeMs();
    int64_t elapsed = currentTime - startTime;

    // Check if session has timed out (2 minutes)
    if (elapsed > getSessionTimeout()) {
        // Session expired - need revalidation
        LOGW("[TIME] FAIL: 会话已超时! 已过=%lld ms > 超时=%lld ms",
             static_cast<long long>(elapsed), static_cast<long long>(getSessionTimeout()));
        return false;
    }

    // Cross-check with render config timestamp
    int64_t configTime = RenderConfig::getLastUpdateTimestamp();

    if (configTime == 0) {
        LOGW("[TIME] FAIL: 配置从未初始化");
        return false;  // Config never initialized
    }

    // ========== 时间交叉验证 ==========
    // 检查心跳包的服务器时间与本地时间的一致性
    int64_t serverSessionStart = m_serverSessionStart.load(std::memory_order_acquire);
    int64_t lastHeartbeat = websocket::packet::s2c::S2CPacketKeepalive::lastActive;
    
    if (lastHeartbeat > 0 && serverSessionStart > 0) {
        // 服务器时间线: lastHeartbeat - serverSessionStart = 服务器认为的会话时长
        int64_t serverElapsed = lastHeartbeat - serverSessionStart;
        // 本地时间线: currentSystemTime - serverSessionStart
        int64_t currentSystemTime = util::DateTimeUtil::systemTime();
        int64_t localElapsedFromServer = currentSystemTime - serverSessionStart;
        
        // 时间漂移检查: 两个时间线应该基本一致
        int64_t drift = std::abs(localElapsedFromServer - serverElapsed);
        
        // 如果服务器认为的会话时长超时，也失败
        if (serverElapsed > getSessionTimeout()) {
            LOGW("[TIME] FAIL: 服务器时间线已超时! serverElapsed=%lld ms",
                 static_cast<long long>(serverElapsed));
            return false;
        }
        
        // 时间漂移过大，可能有人篡改了本地时间
        if (drift > getMaxTimeDrift()) {
            LOGW("[TIME] FAIL: 时间漂移过大! 漂移=%lld ms > 阈值=%lld ms",
                 static_cast<long long>(drift), static_cast<long long>(getMaxTimeDrift()));
            return false;
        }
    }

    return true;
}

bool FrameCounter::isPerformanceAcceptable() {
    // Secondary check disguised as performance monitoring
    return shouldContinueRendering() && RenderConfig::isConfigurationValid();
}

uint32_t FrameCounter::getDecayFactor() {
    // Return a value that decays over time
    // Used for cross-module state synchronization
    int64_t startTime = m_frameStartTime.load(std::memory_order_acquire);
    int64_t currentTime = getCurrentTimeMs();

    if (startTime == 0) return 0;

    int64_t elapsed = currentTime - startTime;

    // Decay factor decreases as session ages
    if (elapsed >= getSessionTimeout()) {
        return 0;  // Fully decayed
    }

    // Return remaining "life" as factor
    uint32_t remaining = static_cast<uint32_t>(
        (getSessionTimeout() - elapsed) * DECAY_THRESHOLD / getSessionTimeout()
    );

    return remaining;
}

bool FrameCounter::checkTemporalIntegrity() {
    // Verify temporal consistency across subsystems
    uint32_t decayFactor = getDecayFactor();

    if (decayFactor == 0) {
        return false;  // Session expired
    }

    // Cross-validate with RenderConfig
    if (!RenderConfig::validateRenderingConfig()) {
        return false;
    }

    // Verify frame count is reasonable
    uint32_t frames = m_totalFrames.load(std::memory_order_acquire);
    int64_t startTime = m_frameStartTime.load(std::memory_order_acquire);
    int64_t elapsed = getCurrentTimeMs() - startTime;

    // Sanity check: at least 1 frame per second average
    if (elapsed > 1000 && frames == 0) {
        return false;  // Suspicious - no frames rendered
    }

    return true;
}

} // namespace team::cool::client::guard
