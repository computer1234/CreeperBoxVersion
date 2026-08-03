#include "JNIHelpers.h"
#include "Memory/GameData.h"
#include "SDK/MobileClient.h"
#include <string>

// NeteaseManager Native Methods
jstring NeteaseManager_encryptHttpContent(JNIEnv *env, jclass clazz, jstring content) {
    if(GameData::base == 0) return env->NewStringUTF("");
    using encryptHttp = std::string (__fastcall*)(std::string &content);
    std::string value = jstringToString(env,content);
    std::string encrypt = reinterpret_cast<encryptHttp>(GameData::base+0xC62F6CC)(value);
    
    // toHexLog implementation inline or reused from JNIUtils if available?
    // It was locally defined in JNIFunc.cpp. I should reimplement it or move it to helpers.
    // For now I will reimplement simple hex conversion here or use a helper if available.
    // Looking at JNIHelpers.h, it might not be there.
    // I'll reimplement it for safety as it's small.
    std::string hexString;
    for (unsigned char c : encrypt) {
        char buffer[3];
        snprintf(buffer, sizeof(buffer), "%02x", c);
        hexString += buffer;
    }
    return env->NewStringUTF(hexString.c_str());
}

jstring NeteaseManager_encryptMessage(JNIEnv *env, jclass clazz, jstring message) {
    if(GameData::base == 0) return env->NewStringUTF("");
    std::string value = jstringToString(env,message);
    using encryptMessage = std::string (__fastcall*)(std::string,int,int);
    std::string encrypt = reinterpret_cast<encryptMessage>(GameData::base+0xC631100)(value,3,9);
    return env->NewStringUTF(encrypt.c_str());
}

jstring NeteaseManager_decryptHttpResponse(JNIEnv *env, jclass clazz, jstring response) {
    if(GameData::base == 0) return env->NewStringUTF("");
    using decryptResponse = std::string (__fastcall*)(std::string&,int);
    std::string value = jstringToString(env,response);
    std::string decrypt = reinterpret_cast<decryptResponse>(GameData::base+0xC630488)(value,0);
    return env->NewStringUTF(decrypt.c_str());
}

void NeteaseManager_setLoginUid(JNIEnv *env, jclass clazz, jstring uid) {
    if(GameData::base == 0) return;
    MobileClient* mobile = MobileClient::singleton();
    if(mobile != nullptr) {
         mobile->mLoginUid = jstringToString(env,uid);
    }
}

static const JNINativeMethod gNeteaseManagerMethods[] = {
        {"a", "(Ljava/lang/String;)Ljava/lang/String;", (void*)NeteaseManager_encryptHttpContent},
        {"b", "(Ljava/lang/String;)Ljava/lang/String;", (void*)NeteaseManager_encryptMessage},
        {"c", "(Ljava/lang/String;)Ljava/lang/String;", (void*)NeteaseManager_decryptHttpResponse},
        {"d", "(Ljava/lang/String;)V", (void*)NeteaseManager_setLoginUid}
};

void register_NeteaseManager(JNIEnv* env) {
    jclass clazz = env->FindClass("helper/creeperbox/NeteaseManager");
    if(clazz) env->RegisterNatives(clazz, gNeteaseManagerMethods, sizeof(gNeteaseManagerMethods) / sizeof(gNeteaseManagerMethods[0]));
}
