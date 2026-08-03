#ifndef JNIHELPERS_H
#define JNIHELPERS_H

#include <jni.h>
#include "SDK/Actor.h"
#include "Utils/Logger.h"
#include "SDK/BlockSource.h"
#include "Memory/GameData.h"
#include "Utils/JNIUtils.h"
#include "SDK/Render2D.h"
#include "SDK/Render3D.h"
#include "Include/Arm64InlineHook/arm64_inlinehook.h"
#include <vector>
#include "JavaData.h"
#include "Memory/Hooks.h"

template <typename TRet>
inline auto getInstance(JNIEnv *env, jobject obj) -> TRet {
    long va = env->GetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "pointer", "J"));
    return reinterpret_cast<TRet>(va);
}

inline auto toVec3(JNIEnv *env, jobject obj) -> Vec3 {
    float x = env->GetFloatField(obj,env->GetFieldID(env->GetObjectClass(obj),"x", "F"));
    float y = env->GetFloatField(obj,env->GetFieldID(env->GetObjectClass(obj),"y", "F"));
    float z = env->GetFloatField(obj,env->GetFieldID(env->GetObjectClass(obj),"z", "F"));
    return {x,y,z};
}

inline jobject fromVec3(JNIEnv *env, Vec3 vec) {
    jclass cls = env->FindClass("helper/creeperbox/sdk/math/Vec3f");
    return env->NewObject(cls, env->GetMethodID(cls, "<init>", "(FFF)V"), vec.x,vec.y, vec.z);
}


inline auto toVec3i(JNIEnv *env, jobject obj) -> Vec3i {
    int x = env->GetIntField(obj,env->GetFieldID(env->GetObjectClass(obj),"x", "I"));
    int y = env->GetIntField(obj,env->GetFieldID(env->GetObjectClass(obj),"y", "I"));
    int z = env->GetIntField(obj,env->GetFieldID(env->GetObjectClass(obj),"z", "I"));
    return {x,y,z};
}

inline jobject fromVec3i(JNIEnv *env, Vec3i vec) {
    jclass cls = env->FindClass("helper/creeperbox/sdk/math/Vec3i");
    return env->NewObject(cls, env->GetMethodID(cls, "<init>", "(III)V"), vec.x,vec.y, vec.z);
}

inline jobject fromVec2(JNIEnv *env, Vec2 vec) {
    jclass cls = env->FindClass("helper/creeperbox/sdk/math/Vec2f");
    return env->NewObject(cls, env->GetMethodID(cls, "<init>", "(FF)V"), vec.x,vec.y);
}

inline auto toVec2(JNIEnv *env, jobject obj) -> Vec2 {
    float x = env->GetFloatField(obj,env->GetFieldID(env->GetObjectClass(obj),"x", "F"));
    float y = env->GetFloatField(obj,env->GetFieldID(env->GetObjectClass(obj),"y", "F"));
    return {x,y};
}

inline jobject fromAABB(JNIEnv *env, AABB aabb) {
    jclass cls = env->FindClass("helper/creeperbox/sdk/math/AxisAlignedBB");
    return env->NewObject(cls, env->GetMethodID(cls, "<init>", "(DDDDDD)V"),(double) aabb.lower.x,(double)aabb.lower.y,(double)aabb.lower.z,(double)aabb.upper.x,(double)aabb.upper.y,(double)aabb.upper.z);
}

#endif // JNIHELPERS_H
