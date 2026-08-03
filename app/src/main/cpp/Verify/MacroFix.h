#ifndef VERIFY_MACROS_H
#define VERIFY_MACROS_H

#if defined(_MSC_VER)
    #define __forceinline __forceinline
    #define _forceinline __forceinline
#elif defined(__GNUC__) || defined(__clang__)
    #define __forceinline inline __attribute__((always_inline))
    #define _forceinline inline __attribute__((always_inline))
#else
    #define __forceinline inline
    #define _forceinline inline
#endif

// Include Logger for LOGW, LOGI, LOGE macros
#include "../../Utils/Logger.h"
// If LOGW is not defined in Logger.h (it has LOGI, LOGE, LOGV, LOGD but maybe not LOGW)
#ifndef LOGW
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#endif

#endif // VERIFY_MACROS_H
