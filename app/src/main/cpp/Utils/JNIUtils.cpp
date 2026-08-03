#include "JNIUtils.h"
#include "Logger.h"
#include "../JavaData.h"


jbyteArray convertStringToByteArray(JNIEnv* env, const std::string* str){
    if(str == nullptr || env == nullptr){
        return nullptr;
    }

    jbyteArray byteArray = env->NewByteArray(str->size());

    if (byteArray == nullptr) {
        return nullptr;
    }

    env->SetByteArrayRegion(byteArray, 0, str->size(), reinterpret_cast<const jbyte*>(str->data()));

    return byteArray;
}

void convertByteArrayToString(JNIEnv* env, jbyteArray byteArray, std::string* resultStr) {

    if (resultStr == nullptr || byteArray == nullptr || env == nullptr) {
        return;
    }

    jsize length = env->GetArrayLength(byteArray);
    if (length == 0) {
        resultStr->clear();
        return;
    }

    resultStr->resize(length);

    env->GetByteArrayRegion(byteArray, 0, length, reinterpret_cast<jbyte*>(&(*resultStr)[0]));
}

std::string jstringToString(JNIEnv *env, jstring jStr) {
    if (jStr == nullptr) {
        return "";
    }
    const char *utfChars = env->GetStringUTFChars(jStr, nullptr);
    if (utfChars == nullptr) {
        // Warning: This throws a C++ exception which might crash if not caught,
        // but keeping original behavior.
        throw std::runtime_error("Failed to convert jstring to UTF-8.");
    }
    std::string result(utfChars);
    env->ReleaseStringUTFChars(jStr, utfChars);
    return result;
}

__thread JNIEnv* tlsEnv = nullptr;

JNIEnv* getEnv() {
    if (tlsEnv == nullptr) {
        JavaData::javaVM->AttachCurrentThread(reinterpret_cast<JNIEnv **>((void **) &tlsEnv), NULL);
    }
    return tlsEnv;
}

void detachThread() {
    if (tlsEnv != nullptr) {
        JavaData::javaVM->DetachCurrentThread();
        tlsEnv = nullptr;
    }
}