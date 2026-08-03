#pragma once

/**
 * InlineVerify.h - 内联验证宏
 * 
 * 目的: 将验证逻辑直接嵌入业务代码，消除单点绕过风险
 * 使用: 在Hook函数中使用这些宏替代performSecurityCheck()调用
 * 
 * 每个宏展开后形成独立的验证代码块，攻击者需要patch每个使用点
 */

#include "../Verify/Guard/FrameCounter.h"
#include "../Verify/Guard/RenderConfig.h"
#include "../Verify/Guard/EnvironmentScanner.h"

namespace team::cool::client::guard {

/**
 * 内联时间验证 - 不调用shouldContinueRendering()
 * 直接在调用点展开验证逻辑
 */
#define INLINE_CHECK_TIME_DECAY(fail_action) \
    do { \
        if (!FrameCounter::isSessionActive()) { fail_action; } \
        auto __fc_start = FrameCounter::getDecayFactor(); \
        if (__fc_start == 0) { fail_action; } \
    } while(0)

/**
 * 内联种子验证 - 不调用validateRenderingConfig()
 * 直接读取种子并计算验证公式
 */
#define INLINE_CHECK_SEED_FORMULA(fail_action) \
    do { \
        uint32_t __rc_a = RenderConfig::getTextureSampling(); \
        uint32_t __rc_b = RenderConfig::getAnisotropicLevel(); \
        uint32_t __rc_c = RenderConfig::getMipmapBias(); \
        uint32_t __rc_d = RenderConfig::getShadowQuality(); \
        /* 公式: (a ^ b) % 17 == (c * d) % 17 */ \
        uint32_t __left = (__rc_a ^ __rc_b) % 17; \
        uint32_t __right = ((__rc_c % 17) * (__rc_d % 17)) % 17; \
        if (__left != __right) { fail_action; } \
        /* 模验证: a % 23 == 0 */ \
        if (__rc_a % 23 != 0) { fail_action; } \
    } while(0)

/**
 * 内联环境验证 - 使用缓存结果
 */
#define INLINE_CHECK_ENVIRONMENT(fail_action) \
    do { \
        if (!EnvironmentScanner::isEnvironmentClean()) { fail_action; } \
    } while(0)

/**
 * 完整内联验证 - 替代performSecurityCheck()
 * 展开所有三个验证模块的核心逻辑
 */
#define INLINE_SECURITY_CHECK(fail_action) \
    do { \
        INLINE_CHECK_TIME_DECAY(fail_action); \
        INLINE_CHECK_SEED_FORMULA(fail_action); \
        INLINE_CHECK_ENVIRONMENT(fail_action); \
    } while(0)

/**
 * 带随机化的内联验证 - 每次调用顺序不同
 * 使用编译时常量使每个调用点的代码略有不同
 */
#define INLINE_SECURITY_CHECK_V1(fail_action) \
    do { \
        INLINE_CHECK_SEED_FORMULA(fail_action); \
        INLINE_CHECK_TIME_DECAY(fail_action); \
        INLINE_CHECK_ENVIRONMENT(fail_action); \
    } while(0)

#define INLINE_SECURITY_CHECK_V2(fail_action) \
    do { \
        INLINE_CHECK_ENVIRONMENT(fail_action); \
        INLINE_CHECK_TIME_DECAY(fail_action); \
        INLINE_CHECK_SEED_FORMULA(fail_action); \
    } while(0)

#define INLINE_SECURITY_CHECK_V3(fail_action) \
    do { \
        INLINE_CHECK_TIME_DECAY(fail_action); \
        INLINE_CHECK_ENVIRONMENT(fail_action); \
        INLINE_CHECK_SEED_FORMULA(fail_action); \
    } while(0)

} // namespace team::cool::client::guard
