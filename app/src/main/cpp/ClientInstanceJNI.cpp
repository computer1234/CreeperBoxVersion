#include "JNIHelpers.h"
#include "Memory/GameData.h"
#include "SDK/ClientInstance.h"
#include "SDK/Render2D.h"

// ClientInstance Native Methods
jfloat ClientInstance_getScale(JNIEnv *env, jclass clazz) {
    if(GameData::clientInstance == nullptr || GameData::clientInstance->getGuiData() == nullptr) return 0.0f;
    return GameData::clientInstance->getGuiData()->scale;
}

static const JNINativeMethod gClientInstanceMethods[] = {
        {"a", "()F", (void*)ClientInstance_getScale}
};

void register_ClientInstance(JNIEnv* env) {
    jclass clazz = env->FindClass("helper/creeperbox/sdk/ClientInstance");
    if(clazz) env->RegisterNatives(clazz, gClientInstanceMethods, sizeof(gClientInstanceMethods) / sizeof(gClientInstanceMethods[0]));
}
