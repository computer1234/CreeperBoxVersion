#include <android/log.h>
#ifndef TROSSENSE_LOGGER_H
#define TROSSENSE_LOGGER_H
#define LOG_TAG "CreeperBox"
//#ifndef NDEBUG
//#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__))
//#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN,    LOG_TAG, __VA_ARGS__))
//#define LOGV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))
//#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG,   LOG_TAG, __VA_ARGS__))
//#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO,    LOG_TAG, __VA_ARGS__))
//#else
// Release Build - 禁用所有日志
    #define LOGE(...) ((void)0)
    #define LOGW(...) ((void)0)
    #define LOGV(...) ((void)0)
    #define LOGD(...) ((void)0)
    #define LOGI(...) ((void)0)
//#endif

#endif
