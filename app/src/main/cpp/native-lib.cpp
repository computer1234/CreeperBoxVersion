#include <jni.h>
#include "Memory/Hooks.h"
#include "Utils/ProcessUtil.h"
#include "JavaData.h"
#include "../Include/openssl/openssl/aes.h"

#include <random>

std::string generateRandomHexString(size_t length) {
    const char hexChars[] = "0123456789abcdef";
    std::string result;
    result.reserve(length);

    std::random_device rd;
    std::mt19937 generator(rd());
    std::uniform_int_distribution<> distribution(0, 15);

    for (size_t i = 0; i < length; ++i) {
        result += hexChars[distribution(generator)];
    }

    return result;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {

    GameData::deviceID = generateRandomHexString(32);
    So so = getSo("libminecraftpe.so");

    if(so.start == 0 && so.end == 0){
        return JNI_VERSION_1_6;
    }

    GameData::base = so.start;
    GameData::end = so.end;

    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    JavaData::Init(vm,env);
    Hooks::Init();

    extern void register_EntityActor(JNIEnv* env);
    extern void register_Block(JNIEnv* env);
    extern void register_Inventory(JNIEnv* env);
    extern void register_Item(JNIEnv* env);
    extern void register_Level(JNIEnv* env);
    extern void register_Render(JNIEnv* env);
    extern void register_Entity(JNIEnv* env);
    extern void register_CreeperBox(JNIEnv* env);
    extern void register_NeteaseManager(JNIEnv* env);
    extern void register_ClientInstance(JNIEnv* env);

    register_EntityActor(env);
    register_Block(env);
    register_Inventory(env);
    register_Item(env);
    register_Level(env);
    register_Render(env);
    register_Entity(env);
    register_CreeperBox(env);
    register_NeteaseManager(env);
    register_ClientInstance(env);

    return JNI_VERSION_1_6;
}
