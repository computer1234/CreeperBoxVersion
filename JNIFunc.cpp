#include <jni.h>
#include "SDK/Actor.h"
#include "Utils/Logger.h"
#include "SDK/BlockSource.h"
#include "Memory/GameData.h"
#include "Utils/JNIUtils.h"
#include "SDK/Render2D.h"
#include "SDK/Render3D.h"
#include "Include/AES_256_ECB.h"
#include "Include/base64.h"
#include "Include/arm64_inlinehook.h"
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


extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_a(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getEntityTypeId();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_b(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isAlive();
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_c(JNIEnv *env, jobject thiz) {
    getInstance<Actor*>(env,thiz)->swing();
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_d(JNIEnv *env, jobject thiz,jboolean is_sprinting) {
    getInstance<Actor*>(env,thiz)->setSprinting(is_sprinting);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_e(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isClientSide();
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_f(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isInvisible();
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_g(JNIEnv *env, jobject thiz, jobject other) {
    return getInstance<Actor*>(env,thiz)->canSeeActor(getInstance<Actor*>(env,other));
}
extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_h(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<Actor*>(env,thiz)->getNameTag()->c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_i(JNIEnv *env, jobject thiz) {
    try {
        return env->NewStringUTF(getInstance<Actor*>(env,thiz)->getNamespace().c_str());
    } catch (...) {
        return env->NewStringUTF("");
    }
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_j(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isOnGround();
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_k(JNIEnv *env, jobject thiz,jboolean on_ground) {
    getInstance<Actor*>(env,thiz)->setOnGround(on_ground);
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_o1(JNIEnv *env, jobject thiz, jobject va) {
    Vec3* vec3 = new Vec3(toVec3(env,va));
    getInstance<Actor*>(env,thiz)->setPos(vec3);
    delete vec3;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_n1(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getPos());
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_l(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getFallDistanceComponent()->fallDistance;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_m(JNIEnv *env, jobject thiz,
                                                                jfloat value) {
    getInstance<Actor*>(env,thiz)->getFallDistanceComponent()->fallDistance = value;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_n(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getRuntimeIDComponent()->runtimeID;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_o(JNIEnv *env, jobject thiz,
                                                             jlong value) {
    getInstance<Actor*>(env,thiz)->getRuntimeIDComponent()->runtimeID = value;
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_p(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorGameTypeComponent()->gameType;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_q(JNIEnv *env, jobject thiz, jint value) {
    getInstance<Actor*>(env,thiz)->getActorGameTypeComponent()->gameType = value;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_r(JNIEnv *env,
                                                                                   jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getBlockMovementSlowdownMultiplierComponent()->slowDownFactor);
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_s(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jobject value) {
    getInstance<Actor*>(env,thiz)->getBlockMovementSlowdownMultiplierComponent()->slowDownFactor = toVec3(env,value);
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_t(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getMaxAutoStepComponent()->stepHeight;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_u(JNIEnv *env, jobject thiz,
                                                               jfloat value) {
    getInstance<Actor*>(env,thiz)->getMaxAutoStepComponent()->stepHeight = value;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_v(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorUniqueIDComponent()->uniqueID;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_w(JNIEnv *env, jobject thiz,
                                                            jlong value) {
    getInstance<Actor*>(env,thiz)->getActorUniqueIDComponent()->uniqueID = value;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_x(JNIEnv *env, jobject thiz,
                                                               jint index) {
    return getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->getAbilityBool(index);
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_y(JNIEnv *env, jobject thiz,
                                                                jint index) {
    return getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->getAbilityFloat(index);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_z(JNIEnv *env, jobject thiz,
                                                               jint index, jboolean value) {
    getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->setAbilityBool(index,value);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_a1(JNIEnv *env, jobject thiz,
                                                                jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->setAbilityFloat(index,value);
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_b1(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yaw;

}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_c1(JNIEnv *env,
                                                                            jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yawPrev;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_d1(JNIEnv *env, jobject thiz,
                                                                        jfloat value) {
    getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yaw = value;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_e1(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jfloat value) {
    getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yawPrev = value;
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_f1(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yaw;
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_g1(JNIEnv *env,
                                                                          jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yawPrev;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_h1(JNIEnv *env, jobject thiz,
                                                                      jfloat value) {
    getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yaw = value;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_i1(JNIEnv *env, jobject thiz,
                                                                          jfloat value) {
    getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yawPrev = value;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_j1(JNIEnv *env, jobject thiz) {
    return fromVec2(env,getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRot);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_k1(JNIEnv *env, jobject thiz) {
    return fromVec2(env,getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRotPrev);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_l1(JNIEnv *env, jobject thiz,
                                                            jobject value) {
    getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRot = toVec2(env,value);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_m1(JNIEnv *env, jobject thiz,
                                                                jobject value) {
    getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRotPrev = toVec2(env,value);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_p1(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getPosPrev());
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_q1(JNIEnv *env, jobject thiz,
                                                           jobject value) {
    getInstance<Actor*>(env,thiz)->getStateVectorComponent()->posPrev = toVec3(env,value);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_r1(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getMotion());
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_v1(JNIEnv *env, jobject thiz,
                                                              jobject size) {
    getInstance<Actor*>(env,thiz)->setSize(toVec2(env,size));
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_w1(JNIEnv *env, jobject thiz) {
    return fromVec2(env,getInstance<Actor*>(env,thiz)->getSize());
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_s1(JNIEnv *env, jobject thiz,
                                                          jobject value) {
    getInstance<Actor*>(env,thiz)->setMotion(toVec3(env,value));
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_t1(JNIEnv *env, jobject thiz, jobject aabb) {
    float minX = env->GetDoubleField(aabb,env->GetFieldID(env->GetObjectClass(aabb),"minX", "D"));
    float minY = env->GetDoubleField(aabb,env->GetFieldID(env->GetObjectClass(aabb),"minY", "D"));
    float minZ = env->GetDoubleField(aabb,env->GetFieldID(env->GetObjectClass(aabb),"minZ", "D"));
    float maxX = env->GetDoubleField(aabb,env->GetFieldID(env->GetObjectClass(aabb),"maxX", "D"));
    float maxY = env->GetDoubleField(aabb,env->GetFieldID(env->GetObjectClass(aabb),"maxY", "D"));
    float maxZ = env->GetDoubleField(aabb,env->GetFieldID(env->GetObjectClass(aabb),"maxZ", "D"));
    AABB* ab = getInstance<Actor*>(env,thiz)->getAABB();
    ab->lower = {minX,minY,minZ};
    ab->upper = {maxX,maxY,maxZ};
}

extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_u1(JNIEnv *env, jobject thiz) {
    return fromAABB(env,*getInstance<Actor*>(env,thiz)->getAABB());
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_x1(JNIEnv *env, jobject thiz, jint index) {
    return getInstance<Actor*>(env,thiz)->getStatusFlag(index);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_y1(JNIEnv *env, jobject thiz, jint index,
                                                              jboolean flag) {
    getInstance<Actor*>(env,thiz)->setStatusFlag(index,flag);
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_z1(JNIEnv *env, jobject thiz,
                                                                         jint index) {
    return getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentValue;
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_a2(JNIEnv *env, jobject thiz,
                                                                     jint index) {
    return getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMinValue;
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_b2(JNIEnv *env, jobject thiz,
                                                                     jint index) {
    return getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMaxValue;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_c2(JNIEnv *env, jobject thiz,
                                                                         jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentValue = value;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_d2(JNIEnv *env, jobject thiz,
                                                                     jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMinValue = value;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_e2(JNIEnv *env, jobject thiz,
                                                                     jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMaxValue = value;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_g2(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getStatusFlag(1);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_f2(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getStatusFlag(3);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_h2(JNIEnv *env, jobject thiz,
                                                            jboolean sneaking) {
    getInstance<Actor*>(env,thiz)->setSneaking(sneaking);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_i2(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isPrimaryLocalPlayer();
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_k2(JNIEnv *env, jobject thiz) {
    Actor* vehicle = getInstance<Actor*>(env,thiz)->getVehicle();
    static jclass cls = nullptr;
    static jmethodID method = nullptr;
    if (cls == nullptr) {
        jclass localCls = env->FindClass("helper/creeperbox/sdk/InstanceGenerator");
        cls = (jclass)env->NewGlobalRef(localCls);
        env->DeleteLocalRef(localCls);
        method = env->GetStaticMethodID(cls,"generatorEntity", "(J)Lhelper/creeperbox/sdk/entity/type/EntityActor;");
    }
    return env->CallStaticObjectMethod(cls,method,reinterpret_cast<jlong>(vehicle));
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_a(JNIEnv *env, jobject thiz,
                                                             jobject other) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->attack(getInstance<Actor*>(env,other));
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_k(JNIEnv *env, jobject thiz,
                                                                     jobject delta) {
    Vec2* d = new Vec2(toVec2(env,delta));
    getInstance<LocalPlayer*>(env,thiz)->applyTurnDelta(d);
    delete d;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_l3(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->start);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_m(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->end);
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_n3(JNIEnv *env, jobject thiz) {
    return (int)getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitType;
}

extern "C"
JNIEXPORT jbyte JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_o(JNIEnv *env, jobject thiz) {
    return (jbyte)getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->face;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_p3(JNIEnv *env, jobject thiz) {
    return fromVec3i(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitBlock);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_q(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitPos);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_r(JNIEnv *env, jobject thiz,
                                                                     jobject vec) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->start = toVec3(env,vec);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_s(JNIEnv *env, jobject thiz,
                                                                   jobject vec) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->end = toVec3(env,vec);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_t(JNIEnv *env, jobject thiz,
                                                                 jint type) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitType = static_cast<HitType>(type);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_u(JNIEnv *env, jobject thiz,
                                                                 jbyte hit_face) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->face = hit_face;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_v(JNIEnv *env, jobject thiz,
                                                                  jobject pos) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitBlock = toVec3i(env,pos);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_w(JNIEnv *env, jobject thiz,
                                                                jobject pos) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitPos = toVec3(env,pos);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_j2(JNIEnv *env, jobject thiz) {
    jclass cls = env->FindClass("helper/creeperbox/sdk/component/MoveInputComponent");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls, "<init>", "()V"));
    MoveInputComponent* input = getInstance<LocalPlayer*>(env,thiz)->getMoveInputHandler();


    jfieldID isSneakDownID = env->GetFieldID(cls, "isSneakDown", "Z");
    env->SetBooleanField(obj,isSneakDownID,input->isSneakDown);

    jfieldID isSneakToggleDownID = env->GetFieldID(cls, "isSneakToggleDown", "Z");
    env->SetBooleanField(obj, isSneakToggleDownID, input->isSneakToggleDown);

    jfieldID isFlyDownSlowDownID = env->GetFieldID(cls, "isFlyDownSlowDown", "Z");
    env->SetBooleanField(obj, isFlyDownSlowDownID, input->isFlyDownSlowDown);

    jfieldID isFlyUpSlowDownID = env->GetFieldID(cls, "isFlyUpSlowDown", "Z");
    env->SetBooleanField(obj, isFlyUpSlowDownID, input->isFlyUpSlowDown);

    jfieldID isAscendScaffoldingDownID = env->GetFieldID(cls, "isAscendScaffoldingDown", "Z");
    env->SetBooleanField(obj, isAscendScaffoldingDownID, input->isAscendScaffoldingDown);

    jfieldID isDescendScaffoldingDownID = env->GetFieldID(cls, "isDescendScaffoldingDown", "Z");
    env->SetBooleanField(obj, isDescendScaffoldingDownID, input->isDescendScaffoldingDown);

    jfieldID isJumpDownID = env->GetFieldID(cls, "isJumpDown", "Z");
    env->SetBooleanField(obj, isJumpDownID, input->isJumpDown);

    jfieldID isSprintDownID = env->GetFieldID(cls, "isSprintDown", "Z");
    env->SetBooleanField(obj, isSprintDownID, input->isSprintDown);

    jfieldID isUpLeftDownID = env->GetFieldID(cls, "isUpLeftDown", "Z");
    env->SetBooleanField(obj, isUpLeftDownID, input->isUpLeftDown);

    jfieldID isUpRightDownID = env->GetFieldID(cls, "isUpRightDown", "Z");
    env->SetBooleanField(obj, isUpRightDownID, input->isUpRightDown);

    jfieldID isUpDownID = env->GetFieldID(cls, "isUpDown", "Z");
    env->SetBooleanField(obj, isUpDownID, input->isUpDown);

    jfieldID isDownDownID = env->GetFieldID(cls, "isDownDown", "Z");
    env->SetBooleanField(obj, isDownDownID, input->isDownDown);

    jfieldID isLeftDownID = env->GetFieldID(cls, "isLeftDown", "Z");
    env->SetBooleanField(obj, isLeftDownID, input->isLeftDown);

    jfieldID isRightDownID = env->GetFieldID(cls, "isRightDown", "Z");
    env->SetBooleanField(obj, isRightDownID, input->isRightDown);

    jfieldID isFlyingAscendDownID = env->GetFieldID(cls, "isFlyingAscendDown", "Z");
    env->SetBooleanField(obj, isFlyingAscendDownID, input->isFlyingAscendDown);

    jfieldID isFlyingDescendDownID = env->GetFieldID(cls, "isFlyingDescendDown", "Z");
    env->SetBooleanField(obj, isFlyingDescendDownID, input->isFlyingDescendDown);

    jfieldID moveSideID = env->GetFieldID(cls, "moveSide", "F");
    env->SetFloatField(obj, moveSideID, input->moveSide);

    jfieldID moveForwardID = env->GetFieldID(cls, "moveForward", "F");
    env->SetFloatField(obj, moveForwardID, input->moveForward);

    return obj;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_sdk_block_Block_a(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<Block*>(env,thiz)->legacy->getNameSpace().c_str());
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_block_Block_b(JNIEnv *env, jobject thiz) {
    Block* block = getInstance<Block*>(env,thiz);
    AABB* aabb = new AABB();

    jobject vec3i = env->GetObjectField(thiz, env->GetFieldID(env->GetObjectClass(thiz), "pos", "Lhelper/creeperbox/sdk/math/Vec3i;"));
    Vec3i* vec = new Vec3i(toVec3i(env,vec3i));
    block->legacy->getCollisionShape(aabb,block,GameData::clientInstance->getRegion(),vec,
                                     0x0);
    jobject objAABB = fromAABB(env,*aabb);
    delete vec;
    delete aabb;
    return objAABB;
}

extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_block_Block_d(JNIEnv *env, jobject thiz) {
    Block* block = getInstance<Block*>(env,thiz);
    return block->mData;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_level_Level_a(JNIEnv *env, jobject thiz, jobject pos) {
    Block* block = GameData::clientInstance->getRegion()->getBlock(toVec3i(env,pos));
    jclass cls = env->FindClass("helper/creeperbox/sdk/block/Block");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(JLhelper/creeperbox/sdk/math/Vec3i;)V"),
                                 reinterpret_cast<long>(block),pos);
    return obj;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_a(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mSolid;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_b(JNIEnv *env, jobject thiz) {
    return !getInstance<Material*>(env,thiz)->mNeverBuildable;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_c(JNIEnv *env, jobject thiz) {
    return true;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_d(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mLiquid;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_e(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mSuperHot;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_f(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mBlocksPrecipitation;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_g(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mBlocksMotion;
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_block_Material_h(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mTranslucency;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_block_Material_i(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mType == Air;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_level_Level_b(JNIEnv *env, jobject thiz, jobject pos) {
    Material* material = GameData::clientInstance->getRegion()->getMaterial(toVec3i(env,pos));
    jclass cls = env->FindClass("helper/creeperbox/sdk/block/Material");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(material));
    return obj;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_b(JNIEnv *env, jobject thiz,
                                                                   jobject pos,
                                                                   jint enum_facing_index) {


    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->destroyBlock(toVec3i(env,pos),enum_facing_index);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_c(JNIEnv *env, jobject thiz,
                                                                        jobject pos,
                                                                        jint enum_facing_index) {
    bool ret = true;
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->startDestroyBlock(toVec3i(env,pos),enum_facing_index,ret);
    return ret;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_d(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jobject pos,
                                                                           jint enum_facing_index) {
    bool ret = true;
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->continueDestroyBlock(toVec3i(env,pos),enum_facing_index,ret);
    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_f(JNIEnv *env, jobject thiz,
                                                                      jobject pos,
                                                                      jint enum_facing_index) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->startBuildBlock(toVec3i(env,pos),enum_facing_index);

}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_g(JNIEnv *env, jobject thiz,
                                                                 jobject pos,
                                                                 jint enum_facing_index,
                                                                 jboolean check_item) {
    Vec3i* vec = new Vec3i(toVec3i(env,pos));
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->buildBlock(vec,enum_facing_index,check_item);
    delete vec;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_h(JNIEnv *env, jobject thiz,
                                                                         jobject pos,
                                                                         jint enum_facing_index) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->continueBuildBlock(toVec3i(env,pos),enum_facing_index);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_i1(JNIEnv *env, jobject thiz) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->stopBuildBlock();
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_l2(JNIEnv *env, jobject thiz) {
    Level* level = getInstance<Actor*>(env,thiz)->getLevel();
    jclass cls = env->FindClass("helper/creeperbox/sdk/level/Level");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(level));
    return obj;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_m2(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<Actor*>(env,thiz)->getItemInHand();
    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(item));
    return obj;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_n2(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<Actor*>(env,thiz)->getItemOffHand();
    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(item));
    return obj;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_o2(JNIEnv *env, jobject thiz, jint slot) {
    ItemStack* item = getInstance<Actor*>(env,thiz)->getArmor(slot);
    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(item));
    return obj;
}
extern "C"
JNIEXPORT jshort JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_a(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->auxValue;
    }
    return 0;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_b(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    return item->isValid();
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_c(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getCount();
    }
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_d(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getItem()->maxStackSize;
    }
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_e(JNIEnv *env, jobject thiz,
                                                                 jint size) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        item->getItem()->maxStackSize = size;
    }

}
extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_f(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return env->NewStringUTF(item->getItem()->name.c_str());
    }
    return env->NewStringUTF("minecraft:air");
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_g(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->isBlock();
    }
    return false;
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_h(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getItem()->itemId;
    }
    return 0;
}


std::string toHexLog(const std::string* str) {
    std::string hexString;
    if (str == nullptr) {
        return std::string();
    }

    for (unsigned char c : *str) {
        char buffer[3];
        snprintf(buffer, sizeof(buffer), "%02x", c);
        hexString += buffer;
    }
    return hexString;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_i(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    static jclass cls = nullptr;
    static jmethodID method = nullptr;
    if (cls == nullptr) {
        jclass localCls = env->FindClass("helper/creeperbox/sdk/InstanceGenerator");
        cls = (jclass)env->NewGlobalRef(localCls);
        env->DeleteLocalRef(localCls);
        method = env->GetStaticMethodID(cls,"generatorItemData",
                                        "([B)Lorg/cloudburstmc/protocol/bedrock/data/inventory/ItemData;");
    }
    BinaryStream* bin = new BinaryStream();
    item->getNetworkItemStackDescriptor().write(bin);
    jobject ret = env->CallStaticObjectMethod(cls,method, convertStringToByteArray(env,bin->buffer));
    delete bin;
    return ret;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_a1(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    CustomWritePacket* packet = new CustomWritePacket(s);
    packet->callEvent = false;
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getPacketSender()->sendToServer(packet);
    env->DeleteLocalRef(data);
    // 不要立即 delete packet，Minecraft 可能异步处理它
    // delete packet;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_x(JNIEnv *env, jobject thiz,
                                                                       jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    CustomWritePacket* packet = new CustomWritePacket(s);
    packet->callEvent = true;
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getPacketSender()->sendToServer(packet);
    env->DeleteLocalRef(data);
    // 不要立即 delete packet，Minecraft 可能异步处理它
    // delete packet;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_z(JNIEnv *env, jobject thiz,
                                                                          jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    GameData::packetList.push_back(new CustomReadPacket(s, true));
    env->DeleteLocalRef(data);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_y(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    GameData::packetList.push_back(new CustomReadPacket(s, false));
    env->DeleteLocalRef(data);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_inventory_PlayerInventory_a(JNIEnv *env, jobject thiz) {
    Inventory* container = getInstance<PlayerInventory*>(env,thiz)->container;
    jclass cls = env->FindClass("helper/creeperbox/sdk/inventory/Container");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(container));
    return obj;
}


extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_inventory_PlayerInventory_b(JNIEnv *env, jobject thiz) {
    return getInstance<PlayerInventory*>(env,thiz)->selectedHotbarSlot;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_inventory_PlayerInventory_c(JNIEnv *env, jobject thiz,
                                                              jint slot) {
    getInstance<PlayerInventory*>(env,thiz)->selectedHotbarSlot = slot;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_g3(JNIEnv *env, jobject thiz) {
    PlayerInventory* inventory = getInstance<LocalPlayer*>(env,thiz)->getInventory();
    jclass cls = env->FindClass("helper/creeperbox/sdk/inventory/PlayerInventory");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(inventory));
    return obj;
}




extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_inventory_Container_a(JNIEnv *env, jobject thiz) {
    return static_cast<jint>(getInstance<Container *>(env, thiz)->containerType);
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_inventory_Container_b(JNIEnv *env, jobject thiz) {
    return getInstance<Container *>(env, thiz)->runtimeID;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_sdk_inventory_Container_c(JNIEnv *env, jobject thiz) {
    return getInstance<Container *>(env, thiz)->hasCustomName;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_sdk_inventory_Container_d(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<Container *>(env, thiz)->name.c_str());
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_inventory_Container_e(JNIEnv *env, jobject thiz) {
    return getInstance<Container *>(env, thiz)->getContainerSize();
}

extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_inventory_Container_f(JNIEnv *env, jobject thiz, jint slot) {
    ItemStack* item = getInstance<Container*>(env,thiz)->getItemStack(slot);
    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(item));
    return obj;
}



extern "C"
JNIEXPORT jobjectArray JNICALL
Java_helper_creeperbox_sdk_level_Level_f(JNIEnv *env, jobject thiz) {

    std::vector<Player*> list;
    getInstance<Level*>(env,thiz)->forEachPlayer([&list](Player &player) {
        list.push_back(&player);
        return true;
    });

    int size = list.size();
    jclass cls = env->FindClass("helper/creeperbox/sdk/entity/type/EntityActor");
    jobjectArray javaArray = env->NewObjectArray(size, cls, nullptr);
    static jclass genCls = nullptr;
    static jmethodID method = nullptr;
    if (genCls == nullptr) {
        jclass localCls = env->FindClass("helper/creeperbox/sdk/InstanceGenerator");
        genCls = (jclass)env->NewGlobalRef(localCls);
        env->DeleteLocalRef(localCls);
        method = env->GetStaticMethodID(genCls,"generatorPlayer", "(J)Lhelper/creeperbox/sdk/entity/type/EntityActor;");
    }
    for (jsize i = 0; i < size; ++i) {
        const Player* actor = list.at(i);
        jobject javaActor = env->CallStaticObjectMethod(genCls,method,reinterpret_cast<jlong>(actor));
        env->SetObjectArrayElement(javaArray, i, javaActor);
    }

    return javaArray;
}


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_helper_creeperbox_sdk_level_Level_g(JNIEnv *env, jobject thiz) {
    auto& map = getInstance<Level*>(env,thiz)->getPlayerList();

    std::vector<std::pair<long, std::string>> pairs;
    pairs.reserve(map.size());

    for (const auto& pair : map) {
        const PlayerListEntry& entry = pair.second;
        pairs.emplace_back(entry.uniqueID, entry.name);
    }
    jclass pairClass = env->FindClass("android/util/Pair");
    jmethodID pairConstructor = env->GetMethodID(
            pairClass, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
    jclass longClass = env->FindClass("java/lang/Long");
    jmethodID longValueOf = env->GetStaticMethodID(
            longClass, "valueOf", "(J)Ljava/lang/Long;");
    jobjectArray result = env->NewObjectArray(
            pairs.size(), pairClass, nullptr);

    for (size_t i = 0; i < pairs.size(); ++i) {
        jobject javaLong = env->CallStaticObjectMethod(
                longClass, longValueOf, pairs[i].first);

        jstring javaString = env->NewStringUTF(pairs[i].second.c_str());

        jobject pairObject = env->NewObject(
                pairClass, pairConstructor, javaLong, javaString);

        env->SetObjectArrayElement(result, i, pairObject);

        env->DeleteLocalRef(javaLong);
        env->DeleteLocalRef(javaString);
        env->DeleteLocalRef(pairObject);
    }
    return result;
}


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_helper_creeperbox_sdk_level_Level_c(JNIEnv *env, jobject thiz) {

    std::vector<Actor*>* list = getInstance<Level*>(env,thiz)->getRuntimeActorList();

    getInstance<Level*>(env,thiz)->forEachPlayer([&list](Player &player) {

        if (std::find(list->begin(), list->end(), &player) == list->end()) {
            list->push_back(&player);
        }
        return true;
    });

    int size = list->size();
    jclass cls = env->FindClass("helper/creeperbox/sdk/entity/type/EntityActor");
    jobjectArray javaArray = env->NewObjectArray(size, cls, nullptr);
    static jclass genCls = nullptr;
    static jmethodID method = nullptr;
    if (genCls == nullptr) {
        jclass localCls = env->FindClass("helper/creeperbox/sdk/InstanceGenerator");
        genCls = (jclass)env->NewGlobalRef(localCls);
        env->DeleteLocalRef(localCls);
        method = env->GetStaticMethodID(genCls,"generatorEntity",
                                        "(J)Lhelper/creeperbox/sdk/entity/type/EntityActor;");
    }

    for (jsize i = 0; i < size; ++i) {
        Actor* actor = list->at(i);
        jobject javaActor = env->CallStaticObjectMethod(genCls,method,reinterpret_cast<jlong>(actor));
        env->SetObjectArrayElement(javaArray, i, javaActor);
    }

    return javaArray;
}


std::string jstringToString(JNIEnv *env, jstring jStr) {

    if (jStr == nullptr) {
        return "";
    }

    const char *utfChars = env->GetStringUTFChars(jStr, nullptr);
    if (utfChars == nullptr) {
        throw std::runtime_error("Failed to convert jstring to UTF-8.");
    }

    std::string result(utfChars);
    env->ReleaseStringUTFChars(jStr, utfChars);
    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_b1(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jstring msg) {
    getInstance<LocalPlayer*>(env,thiz)->displayClientMessage(jstringToString(env,msg));
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_render_ScreenView_a(JNIEnv *env, jobject thiz) {
    getInstance<ScreenView*>(env,thiz)->currentController->tryExit();
}


extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_sdk_render_ScreenView_b(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<ScreenView*>(env,thiz)->visualTree->mRootControl->name.c_str());
}



extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_clients_CreeperBox_getLocalPlayer(JNIEnv *env, jobject thiz) {

    if(GameData::clientInstance == nullptr){
        return nullptr;
    }

    LocalPlayer* player = GameData::clientInstance->getLocalPlayer();
    if(player == nullptr){
        return nullptr;
    }
    jclass cls = env->FindClass("helper/creeperbox/sdk/entity/type/EntityLocalPlayer");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(player));

    return obj;
}



extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_c1(JNIEnv *env, jclass thiz,
                                                                jboolean no_slow) {
    GameData::doNoSlow = no_slow;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_d1(JNIEnv *env, jclass clazz,
                                                                 jboolean inv_move) {
    GameData::doInvMove = inv_move;
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_e1(JNIEnv *env,
                                                                         jobject thiz,
                                                                         jobject block) {
    return getInstance<LocalPlayer *>(env, thiz)->getDestroyRate(getInstance<Block*>(env, block));
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_f1(JNIEnv *env, jclass clazz,
                                                               jfloat timer) {
    if(GameData::clientInstance != nullptr){
        GameData::clientInstance->minecraft->setTimer(timer);
    }
}





#include <functional>
#include <fstream>
#include <sstream>





std::string base64Encode(const unsigned char* data, size_t length) {
    size_t encodedSize = BASE64_ENCODE_OUT_SIZE(length);
    std::vector<char> encodedBuffer(encodedSize);
    base64_encode(data, length, encodedBuffer.data());
    return std::string(encodedBuffer.data(), encodedSize - 1);
}


std::string encryptAES(const std::string &data) {

    unsigned char key[32] = {
            0x85, 0xc1, 0x73, 0xe0, 0x6d, 0x4a, 0x2f, 0xb9,
            0x1d, 0x58, 0x0c, 0xf6, 0x3b, 0x49, 0x27, 0xed,
            0xc1, 0xa0, 0x75, 0xd2, 0xe3, 0x64, 0xc1, 0x80,
            0x95, 0x72, 0xf3, 0xb1, 0xd0, 0xa4, 0x85, 0xf6,
    };

    for (unsigned char & i : key) {
        i = i ^ 0xFF;
    }

    AES_CTX ctx;
    unsigned char keyBuffer[AES_KEY_SIZE];
    memcpy(keyBuffer, key, AES_KEY_SIZE);

    size_t paddedLength = ((data.size() / AES_BLOCK_SIZE) + 1) * AES_BLOCK_SIZE;
    std::vector<unsigned char> dataBuffer(paddedLength, 0);

    memcpy(dataBuffer.data(), data.data(), data.size());
    size_t paddingValue = paddedLength - data.size();
    std::fill(dataBuffer.begin() + data.size(), dataBuffer.end(), paddingValue);

    std::vector<unsigned char> encryptedBuffer(paddedLength, 0);
    AES_EncryptInit(&ctx, keyBuffer);
    for (size_t i = 0; i < paddedLength; i += AES_BLOCK_SIZE) {
        AES_Encrypt(&ctx, dataBuffer.data() + i, encryptedBuffer.data() + i);
    }
    AES_CTX_Free(&ctx);

    return base64Encode(encryptedBuffer.data(), encryptedBuffer.size());
}



std::vector<unsigned char> base64Decode(const std::string& encoded) {
    const size_t max_decoded_size = BASE64_DECODE_OUT_SIZE(encoded.size());
    std::vector<unsigned char> decoded(max_decoded_size, 0);

    const unsigned int actual_size = base64_decode(
            encoded.data(),
            static_cast<unsigned int>(encoded.size()),
            decoded.data()
    );

    if ((actual_size == 0 && !encoded.empty()) || actual_size > max_decoded_size) {
        return decoded;
    }

    decoded.resize(actual_size);

    return decoded;
}


std::string decryptAES(const std::string &encryptedData) {

    unsigned char key[32] = {
            0x85, 0xc1, 0x73, 0xe0, 0x6d, 0x4a, 0x2f, 0xb9,
            0x1d, 0x58, 0x0c, 0xf6, 0x3b, 0x49, 0x27, 0xed,
            0xc1, 0xa0, 0x75, 0xd2, 0xe3, 0x64, 0xc1, 0x80,
            0x95, 0x72, 0xf3, 0xb1, 0xd0, 0xa4, 0x85, 0xf6,
    };

    for (unsigned char & i : key) {
        i = i ^ 0x30;
    }

    std::vector<unsigned char> encryptedBuffer = base64Decode(encryptedData);

    if (encryptedBuffer.size() % AES_BLOCK_SIZE != 0) {
        return "";
    }

    AES_CTX ctx;
    unsigned char keyBuffer[AES_KEY_SIZE];
    memcpy(keyBuffer, key, AES_KEY_SIZE);

    AES_DecryptInit(&ctx, keyBuffer);

    std::vector<unsigned char> decryptedBuffer(encryptedBuffer.size(), 0);
    for (size_t i = 0; i < encryptedBuffer.size(); i += AES_BLOCK_SIZE) {
        AES_Decrypt(&ctx, encryptedBuffer.data() + i, decryptedBuffer.data() + i);
    }
    AES_CTX_Free(&ctx);

    if (decryptedBuffer.empty()) {
        return "";
    }

    size_t paddingValue = decryptedBuffer.back();
    if (paddingValue == 0 || paddingValue > AES_BLOCK_SIZE) {
        return "";
    }

    if (decryptedBuffer.size() < paddingValue) {
        return "";
    }

    for (size_t i = decryptedBuffer.size() - paddingValue; i < decryptedBuffer.size(); ++i) {
        if (decryptedBuffer[i] != paddingValue) {
            return "";
        }
    }

    decryptedBuffer.resize(decryptedBuffer.size() - paddingValue);
    return std::string(decryptedBuffer.begin(), decryptedBuffer.end());
}


void get_device_info(char *output_hash) {

    char model[128] = "0";
    __system_property_get("ro.product.model", model);
    char board[128] = "0";
    __system_property_get("ro.product.board", board);
    char device[128] = "0";
    __system_property_get("ro.product.device", device);


    std::string _model = model;
    _model = _model.substr(0, 8);
    std::string _board = board;
    _board = _board.substr(0, 8);;
    std::string _device = device;
    _device = _device.substr(0, 8);
    std::string baseCode =  _model + _board + _device + _model + _board + _device;

    snprintf(output_hash, 17, "%s%s%s%s",
             baseCode.substr(0, 4).c_str(),
             baseCode.substr(3, 4).c_str(),
             baseCode.substr(7, 4).c_str(),
             baseCode.substr(11, 4).c_str());

}




extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_VerifyManager_a(JNIEnv *env, jclass clazz, jstring card
) {

    char hash_output[20];
    get_device_info(hash_output);

    auto now = std::chrono::system_clock::now();
    auto milliseconds = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch()).count();

    std::stringstream ss;
    ss << milliseconds << " " << jstringToString(env,card) << " " << std::string(hash_output);
    std::string out = ss.str();
    std::string value = encryptAES(out);

    return env->NewStringUTF(value.c_str());
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_VerifyManager_b(JNIEnv *env, jclass clazz, jstring data) {

    std::string result = decryptAES(jstringToString(env,data));
    if(!result.empty()){
        std::istringstream iss(result);
        long long time;
        iss >> time;
        auto now = std::chrono::system_clock::now();
        auto milliseconds = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch()).count();

        if((time+1)%13!=0){
            GameData::test = true;
        }

        std::srand(std::time(0));

        long randomIV = 0;
        for (int i = 0; i < sizeof(long) / sizeof(int); i++) {
            randomIV = (randomIV << (sizeof(int) * 8)) | std::rand();
        }

        GameData::randomIV = randomIV;
        getEnv()->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onActorTickMethod,randomIV);

        if(abs(milliseconds-time)>1000*60){
            return true;
        }

        long long verifyTime;
        iss >> verifyTime;
        GameData::verifyTime = verifyTime;
        GameData::setVerify = 1;
        GameData::verify = 6500;
    }

    return true;
}



extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_e2(JNIEnv *env, jobject thiz, jobject pos) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->stopDestroyBlock(toVec3i(env,pos));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_helper_creeperbox_sdk_level_Level_d(JNIEnv *env, jobject thiz) {
    return getInstance<Level*>(env,thiz)->getCurrentTick()->tick;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_level_Level_e(JNIEnv *env, jobject thiz, jlong tick) {
    getInstance<Level*>(env,thiz)->getCurrentTick()->tick = tick;
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_block_Block_c(JNIEnv *env, jobject thiz) {
    return getInstance<Block*>(env,thiz)->runtimeID;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_render_Camera_a(JNIEnv *env, jobject thiz) {
    float* m = getInstance<mce::Camera*>(env, thiz)->viewMatrixStack.stack.top().m;

    jfloatArray matrixArray = env->NewFloatArray(16);
    if (matrixArray == nullptr) {
        return nullptr;
    }

    env->SetFloatArrayRegion(matrixArray, 0, 16, m);

    jclass matrixClass = env->FindClass("helper/creeperbox/sdk/math/Matrix");
    if (matrixClass == nullptr) {
        return nullptr;
    }

    jmethodID constructor = env->GetMethodID(matrixClass, "<init>", "([F)V");
    if (constructor == nullptr) {
        return nullptr;
    }

    jobject matrixObject = env->NewObject(matrixClass, constructor, matrixArray);

    env->DeleteLocalRef(matrixArray);
    env->DeleteLocalRef(matrixClass);

    return matrixObject;

}
extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_render_ScreenContext_a(JNIEnv *env, jobject thiz) {
    return getInstance<ScreenContext*>(env, thiz)->partialTicks;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_render_ScreenContext_b(JNIEnv *env, jobject thiz) {
    mce::Camera* camera = getInstance<ScreenContext*>(env, thiz)->camera;
    jclass cls = env->FindClass("helper/creeperbox/sdk/render/Camera");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(camera));
    return obj;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_render_Camera_b(JNIEnv *env, jobject thiz) {
    float* m = getInstance<mce::Camera*>(env, thiz)->modelMatrixStack.stack.top().m;

    jfloatArray matrixArray = env->NewFloatArray(16);
    if (matrixArray == nullptr) {
        return nullptr;
    }

    env->SetFloatArrayRegion(matrixArray, 0, 16, m);

    jclass matrixClass = env->FindClass("helper/creeperbox/sdk/math/Matrix");
    if (matrixClass == nullptr) {
        return nullptr;
    }

    jmethodID constructor = env->GetMethodID(matrixClass, "<init>", "([F)V");
    if (constructor == nullptr) {
        return nullptr;
    }

    jobject matrixObject = env->NewObject(matrixClass, constructor, matrixArray);

    env->DeleteLocalRef(matrixArray);
    env->DeleteLocalRef(matrixClass);

    return matrixObject;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_render_Camera_c(JNIEnv *env, jobject thiz) {
    float* m = getInstance<mce::Camera*>(env, thiz)->projectionMatrixStack.stack.top().m;

    jfloatArray matrixArray = env->NewFloatArray(16);
    if (matrixArray == nullptr) {
        return nullptr;
    }

    env->SetFloatArrayRegion(matrixArray, 0, 16, m);

    jclass matrixClass = env->FindClass("helper/creeperbox/sdk/math/Matrix");
    if (matrixClass == nullptr) {
        return nullptr;
    }

    jmethodID constructor = env->GetMethodID(matrixClass, "<init>", "([F)V");
    if (constructor == nullptr) {
        return nullptr;
    }

    jobject matrixObject = env->NewObject(matrixClass, constructor, matrixArray);

    env->DeleteLocalRef(matrixArray);
    env->DeleteLocalRef(matrixClass);

    return matrixObject;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_render_LevelRenderer_a(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LevelRenderer*>(env,thiz)->levelRendererPlayer->cameraPos);
}


extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_render_LevelRenderer_b(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LevelRenderer*>(env,thiz)->levelRendererPlayer->targetCameraPos);
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_render_UIRenderContext_a(JNIEnv *env, jobject thiz, jobject item, jfloat x,
                                                jfloat y, jfloat scale, jfloat opacity, jfloat idk,
                                                jboolean enchantment) {
    ItemStack *i = getInstance<ItemStack *>(env, item);
    if (i->isValid()) {
        UIRenderContext *ctx = getInstance<UIRenderContext *>(env, thiz);
        BaseActorRenderContext baseActorRenderContext = BaseActorRenderContext(ctx->screenContext);
        baseActorRenderContext.renderer->renderGuiItemNew(&baseActorRenderContext, i, 0, x, y,
                                                          opacity, scale, idk,
                                                          enchantment);
    }
}
extern "C"
JNIEXPORT jobject JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityPlayer_ab(JNIEnv *env, jobject thiz) {
    SerializedSkin* skin = getInstance<Player*>(env,thiz)->getSkin();
    static jclass cls = nullptr;
    static jmethodID method = nullptr;
    if (cls == nullptr) {
        jclass localCls = env->FindClass("helper/creeperbox/sdk/InstanceGenerator");
        cls = (jclass)env->NewGlobalRef(localCls);
        env->DeleteLocalRef(localCls);
        method = env->GetStaticMethodID(cls,"generatorSkin", "([B)Lorg/cloudburstmc/protocol/bedrock/data/skin/SerializedSkin;");
    }
    BinaryStream stream = BinaryStream();
    skin->write(&stream);
    jobject ret = env->CallStaticObjectMethod(cls,method, convertStringToByteArray(env,stream.buffer));
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_item_ItemStack_j(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getItem()->getMaxDamage();
    }
    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_render_UIRenderContext_b(JNIEnv *env, jobject thiz, jbyteArray b,
                                                jobject player, jfloat x, jfloat y, jfloat scale,
                                                jfloat opacity, jfloat idk, jboolean enchantment) {
    int maxDamage = 0;
    BinaryStream* bin = new BinaryStream();
    convertByteArrayToString(env,b,bin->buffer);
    NetworkItemStackDescriptor descriptor = NetworkItemStackDescriptor();
    bin->readItem(&descriptor);
    ItemStack i = ItemStack();
    i.fromDescriptor(&descriptor, getInstance<LocalPlayer*>(env,player)->getLevel()->getBlockPalette(), false);
    if (i.isValid()) {
        i.pickUpTime = 0;
        UIRenderContext *ctx = getInstance<UIRenderContext *>(env, thiz);
        BaseActorRenderContext baseActorRenderContext = BaseActorRenderContext(ctx->screenContext);
        baseActorRenderContext.renderer->renderGuiItemNew(&baseActorRenderContext, &i, 0, x, y,
                                                          opacity, scale, idk,
                                                          enchantment);
        maxDamage = i.getItem()->getMaxDamage();
    }
    delete bin;
    return maxDamage;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_g1(JNIEnv *env, jobject thiz, jint tick) {
    getInstance<LocalPlayer*>(env,thiz)->swingTick = tick;
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_h1(JNIEnv *env, jobject thiz) {
    return getInstance<LocalPlayer*>(env,thiz)->swingTick;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_i2(JNIEnv *env, jclass clazz,
                                                        jboolean item_no_rot) {
    GameData::doItemNoRot = item_no_rot;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_clients_CreeperBox_a(JNIEnv *env, jobject thiz, jstring cmd) {
    
    *reinterpret_cast<bool*>(GameData::base+0xE02CBA0) = false;    //exec failed

    using ensureGIL = void* (__fastcall*)();
    void* gState = reinterpret_cast<ensureGIL>(GameData::base+0xCEEA0E0)();    //Couldn't create thread-state for new thread

    using pyAddModule = void* (__fastcall*)(const char*);
    void* _module = reinterpret_cast<pyAddModule>(GameData::base+0xCE69C64)("__main__");    //import %s # directory %s\n     找调用上一个
    using pyGetDict = void* (__fastcall*)(void*);
    void* dist = reinterpret_cast<pyGetDict>(GameData::base+0xCDDEA7C)(_module);   //E:/base/release/minecraftPE/handheld/src-plugins/python/Objects/moduleobject.c


    const char *command = env->GetStringUTFChars(cmd,NULL);
    using pyRun = int (__fastcall*)(const char *code,int start,void*,void*,void*);
    int result = reinterpret_cast<pyRun>(GameData::base+0xCEEB9FC)(command,0x101,dist,dist,NULL);    //<string>

    env->ReleaseStringUTFChars(cmd,command);

    using releaseEGL = void* (__fastcall*)(void*);
    reinterpret_cast<releaseEGL>(GameData::base+0xCEEA178)(gState);      //auto-releasing thread-state, but no thread-state for this thread

    return result != 0;
}




extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_b(JNIEnv *env, jobject thiz, jstring cookie) {
    GameData::cookie = jstringToString(env,cookie);
}


extern "C"
JNIEXPORT jfloat JNICALL
Java_helper_creeperbox_sdk_ClientInstance_a(JNIEnv *env, jclass clazz) {
    return GameData::clientInstance->getGuiData()->scale;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_j4(JNIEnv *env, jclass clazz, jboolean noweb) {
    GameData::doNoWeb = noweb;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_j5(JNIEnv *env, jobject thiz, jint delay) {
    getInstance<LocalPlayer*>(env,thiz)->getMobJumpComponent()->jumpDelay = delay;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_clients_CreeperBox_c(JNIEnv *env, jobject thiz) {
    return GameData::clientInstance->getGuiData()->showProgress;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_d(JNIEnv *env, jobject thiz) {
    GameData::clientInstance->handleDestroyOrAttackButtonPress();
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_e(JNIEnv *env, jobject thiz) {
    GameData::clientInstance->handleBuildOrInteractButtonPress();
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_clients_CreeperBox_f(JNIEnv *env, jobject thiz) {
    return GameData::clientInstance->getGuiData()->pointerX;
}

extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_clients_CreeperBox_g(JNIEnv *env, jobject thiz) {
    return GameData::clientInstance->getGuiData()->pointerY;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_f4(JNIEnv *env, jclass clazz, jfloat range) {
    GameData::reach = range;
}

extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_f5(JNIEnv *env, jclass clazz, jfloat range) {
    GameData::buildReach = range;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_f6(JNIEnv *env, jclass clazz,
                                                            jboolean force) {
    GameData::forcePos = force;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_clients_CreeperBox_h(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(GameData::lastIP.c_str());
}


class MobileClient {
public:
    char padding0000[0x10];
    unsigned int mFrames;
    float mFps;
    float mFrameTime;
    int mPadding001C;
    char padding0020[0x20];
    std::string mLoginUid;    //0x40
    char padding0058[0x90];
    std::string mRoomSid;     //0xE8

    static MobileClient* singleton(){
        using getMobileClient = MobileClient* (__fastcall*)();
        return reinterpret_cast<getMobileClient>(GameData::base+0xA372CFC)();
    }
};

static_assert(offsetof(MobileClient,mLoginUid)==0x40);


extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_clients_CreeperBox_i(JNIEnv *env, jobject thiz) {
    using getMobileClient = MobileClient* (__fastcall*)();
    MobileClient* mobile = MobileClient::singleton();
    return env->NewStringUTF(mobile->mRoomSid.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_j(JNIEnv *env, jobject thiz, jstring sid) {
    MobileClient* mobile = MobileClient::singleton();
    mobile->mRoomSid = jstringToString(env,sid);
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_k(JNIEnv *env, jobject thiz, jstring ip) {
    GameData::nextIP = jstringToString(env,ip);
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_l(JNIEnv *env, jobject thiz, jint port) {
    GameData::nextPort = port;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_NeteaseManager_a(JNIEnv *env, jclass clazz, jstring content) {
    using encryptHttp = std::string (__fastcall*)(std::string &content);
    std::string value = jstringToString(env,content);
    std::string encrypt = reinterpret_cast<encryptHttp>(GameData::base+0xA568FB0)(value);
    std::string hex = toHexLog(&encrypt);
    return env->NewStringUTF(hex.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_NeteaseManager_b(JNIEnv *env, jclass clazz, jstring message) {
    std::string value = jstringToString(env,message);
    using encryptMessage = std::string (__fastcall*)(std::string,int,int);
    std::string encrypt = reinterpret_cast<encryptMessage>(GameData::base+0xA56A9E8)(value,4,9);
    return env->NewStringUTF(encrypt.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_NeteaseManager_d(JNIEnv *env, jclass clazz, jstring uid) {
    MobileClient* mobile = MobileClient::singleton();
    mobile->mLoginUid = jstringToString(env,uid);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_helper_creeperbox_NeteaseManager_c(JNIEnv *env, jclass clazz, jstring response) {
    using decryptResponse = std::string (__fastcall*)(std::string&,int);
    std::string value = jstringToString(env,response);
    std::string decrypt = reinterpret_cast<decryptResponse>(GameData::base+0xA569D70)(value,0);
    return env->NewStringUTF(decrypt.c_str());
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityLocalPlayer_f7(JNIEnv *env, jclass clazz) {
    if(GameData::clientInstance == nullptr) return 0;
    return GameData::clientInstance->getOptions()->getPlayerViewPerspective();
}

extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_clients_CreeperBox_m(JNIEnv *env, jclass clazz) {
    return *reinterpret_cast<int*>(GameData::base+0xDF274D0);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_n(JNIEnv *env, jclass clazz,jint id) {
    *reinterpret_cast<int*>(GameData::base+0xDF274D0) = id;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_sdk_render_LevelRenderer_c(JNIEnv *env, jobject thiz) {
    getInstance<LevelRenderer*>(env,thiz)->levelRendererPlayer->_ResetArea();
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_o(JNIEnv *env, jclass clazz, jboolean do_xray) {
    GameData::doXRay = do_xray;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_p(JNIEnv *env, jclass clazz, jboolean do_xray) {
    GameData::doChestXray = do_xray;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_q(JNIEnv *env, jclass clazz, jstring name) {
    GameData::xRayList.push_back(jstringToString(env,name));
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_r(JNIEnv *env, jclass clazz, jstring name) {
    GameData::xRayList.erase(std::remove(GameData::xRayList.begin(), GameData::xRayList.end(),jstringToString(env,name)), GameData::xRayList.end());
}
extern "C"
JNIEXPORT jint JNICALL
Java_helper_creeperbox_clients_CreeperBox_s(JNIEnv *env, jclass clazz) {
    return (int)MobileClient::singleton()->mFps;
}
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_helper_creeperbox_sdk_entity_type_EntityActor_p2(JNIEnv *env, jobject thiz) {


    std::vector<Vec3i> list;

    for(auto& effect : getInstance<Actor*>(env,thiz)->getEffect()->mMobEffects){
        if(effect.mId!=0){
            list.push_back(Vec3i(effect.mId,effect.mDuration,effect.mAmplifier));
        }
    }

    int size = list.size();
    jclass cls = env->FindClass("helper/creeperbox/sdk/math/Vec3i");
    jobjectArray javaArray = env->NewObjectArray(size, cls, nullptr);
    for (jsize i = 0; i < size; ++i) {
        Vec3i v = list.at(i);
        env->SetObjectArrayElement(javaArray, i, fromVec3i(env,v));
    }

    return javaArray;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_t(JNIEnv *env, jclass clazz, jfloat gamma) {
    GameData::gamma = gamma;
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_u(JNIEnv *env, jclass clazz) {
    Arm64InlineHook* hooker = Arm64InlineHook::getInstance();
    hooker->reset();
}


extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_bp_v(JNIEnv *env, jobject thiz, jlong data) {

    int64_t list[] = {
            6214082114047720915LL, -4471776462721843403LL, 4441441735120650868LL, 3247481921259220196LL,
            1035228910821204767LL, 3933671153215991979LL, -6689469046797309893LL, -7684828068386732173LL,
            -1569280641528304514LL, 8236818374703279354LL, -3586638027243387363LL, -7096656257359784806LL,
            -7849850181252998005LL, 4231389445899312642LL, 6368499544738036942LL, -1453725290493663153LL,
            -669803180200291797LL, -1337142603085888341LL, 9115144573303738527LL, -8769768784339665433LL,
            5463176200268462097LL, 6971836013165188702LL, 6781891442445354708LL, -3212878721463786264LL,
            5218763045095788440LL, -7413529439938301970LL, -7231125936253309242LL, -8520064047642862186LL,
            4148297738362065096LL, -8651335943029824160LL, 7591113397582917661LL, 8296450355849614923LL,
            -8782206477479218258LL, -5312604310344252503LL, -5252946394672127523LL, 7792742945529987548LL,
            -8754630044147731461LL, -95584764095838369LL, 5187526392428558641LL, 3124750387877619938LL,
            -5184786750387458090LL, -4551485632138718895LL, 7082203077277167788LL, 3580292191687710819LL,
            -3733245531320138608LL, -6810258012148604422LL, -4189588251043326712LL, -8248030466923343683LL,
            8191234724968466232LL, 7204402417630671007LL, -1634585192488675262LL, 9024558753329695689LL,
            -5680019981749609911LL, -3522679388251822647LL, -6770238458389107193LL, -337424156149883296LL,
            5700377958094818323LL, 511560435502001450LL, -1702541513748513386LL, -8187126225339248832LL,
            -8058202322089602040LL, 7036210631793559419LL, -2733310887306914355LL, -8004043383172677811LL,
            -784828649743003697LL, -593435486451241750LL, 5036667469483511279LL, 1315129968723736014LL,
            -723409502731487080LL, -5651598880760087636LL, -6346095762444176800LL, 2386918004688836621LL,
            -887625268438824136LL, 6949699463619789036LL, -8864763681509429865LL, -7496973662698491321LL,
            4034311579910567476LL, 5725148861984305417LL, 4957206421406371727LL, -4541948204740217753LL,
            8437482666001784173LL, -5404063268943982010LL, -2670762415425154007LL, 9043630542744567663LL,
            -7059238319595477842LL, -3981467109481086019LL, 7414471405956790018LL, -1085556406389279454LL,
            -7855112496051426944LL, 4292953508745007185LL, -5727399511015114489LL, 291296236642219375LL,
            -3019210964031786476LL, 218064569494042128LL, 1232003567270456501LL, 2199826865061641782LL,
            -5799827135915830389LL, -4991442824938732541LL, -3058314148603466745LL, 3551658566603287014LL,
            -102585133215631491LL, 8613423984768860605LL, -6249893380356240597LL, 2092090050100552692LL,
            4413872325348663693LL, 8538340378321931389LL, 8892505689293893010LL, 3839426404554059292LL,
            3920304504680142543LL, 6903655074885555344LL, -2688609392776561103LL, -5762687943740824941LL,
            6567699359405685266LL, 2688390521403847762LL, 2253636348287170795LL, 70289076737825684LL,
            1407261727500866003LL, -1701971073759081699LL, -1563415780635425455LL, -5183598946358196653LL,
            -5821377558978181302LL, -684400061244581006LL, 5361519325911424501LL, -8077564594796050968LL,
            -2671622260326325139LL, -1439702361867304206LL, 8019854080440576707LL, 17284058455273164LL,
            -6274889923242768681LL, 563979300644622077LL, 505826905241061086LL, 1284649758295872556LL,
            -4906876734454602200LL, -6355255722957062695LL, 2763106196714781985LL, -2956857257428019513LL,
            3640137734000725465LL, -5947121601609830340LL, 9193651605781895943LL, 6116063688929594402LL,
            -7823133798069122502LL, 3854817847963888430LL, -3524273947572848906LL, 7147922891848647017LL,
            5232311709073342036LL, 7355017633426461688LL, 932711495879805306LL, 1898732153634072963LL,
            -3506057898781222291LL, -4724292816762461455LL, 321299862634554489LL, -4959110566725969878LL,
            1530908890418415616LL, -6358088339982240857LL, 8711909711091866372LL, 651276002122611061LL,
            -8255110975652497810LL, -5114463381252473180LL, 9161620206736887548LL, -4744206924283483678LL,
            -4669016226282663256LL, 1574829245591879901LL, -7071363016318197841LL, -5725848342824751672LL,
            -7619178690014646223LL, 5362599433296894722LL, 7411754193011656384LL, 7393478095057919671LL,
            -299156962141144991LL, 186962053091339415LL, 166718702163845801LL, -8381271052602516799LL,
            -5431969097154207559LL, 3068374692112441245LL, -4406209793716297197LL, 1184348184798780100LL,
            2701603256988516654LL, -37415483324815637LL, -305174809912692801LL, 2768922760838339533LL,
            9130736553018590458LL, 6529268829402158039LL, 7709472607156520429LL, 4411066886970209815LL,
            1813002100984304035LL, 4878018880254745862LL, 5522840514358611366LL, -3581205219458633185LL,
            6230988543990755557LL, -5359791833095899378LL, -5379096817389308788LL, -5729627396977483143LL,
            516157650188971995LL, 8796972693127949600LL, 7137354960005145623LL, -7437890461587416982LL,
            -648448080409548922LL, 9082728299668218668LL, 3303652014196876314LL, 2303759513386028107LL,
            3096580104479318168LL, 4375439750465031042LL, 1799230791595842942LL, 3598424229403503960LL,
            -1920839071182557774LL, 7927164379174657459LL, 35754042622070040LL, 4941887371462368645LL,
            5972901987033414386LL, -6326273944380736508LL, -6996684388090528311LL, -3026800456878339774LL,
            5037032319408312550LL, 9054079621251483442LL, -3745222372696577300LL, -565627696107007556LL,
            6415649400191207412LL, -5632440348143969830LL, 8667558734399850974LL, -207618208301737068LL,
            -6338507787144294396LL, -5378598064597668465LL, -8068001816371028205LL, 8556539786902449532LL,
            -4936729730499945824LL, -3670884478127704220LL, -2678992364236213341LL, 722451818806695934LL,
            2600296175292757888LL, 8568813617425964444LL, -9120050975925638879LL, -5681994468754319251LL,
            8461471299731671424LL, 7958457746081049585LL, 5016765237333347949LL, -2682654244445666795LL,
            3831200886574762883LL, 6003475761117681120LL, -8618339659673056643LL, 5927289012699090364LL,
            -8175725037260152758LL, 5818104123697992010LL, 6723888688001636318LL, 2395103223925712447LL,
            -2741782030285080317LL, -6512142561502913786LL, 6257242320812412874LL, 3081094677685492975LL,
            -7813296763439129753LL, 8846338487506647495LL, -1325073826789879774LL, -579263445362694256LL,
            -6305404896151200882LL, -3297866158058102544LL, -4457155311625063350LL, -4962540244879089228LL
    };

    int64_t request = GameData::randomIV;
    int64_t response = data;
    int64_t key = list[(unsigned short)(request >> 56)];

    int16_t partIV[4];
    partIV[2] = (int16_t)((request >> 48) & 0xFFFF);
    partIV[1] = (int16_t)((request >> 32) & 0xFFFF);
    partIV[3] = (int16_t)((request >> 16) & 0xFFFF);
    partIV[0] = (int16_t)(request & 0xFFFF);

    int16_t partKey[4];
    partKey[0] = (int16_t)((key >> 48) & 0xFFFF);
    partKey[1] = (int16_t)((key >> 32) & 0xFFFF);
    partKey[2] = (int16_t)((key >> 16) & 0xFFFF);
    partKey[3] = (int16_t)(key & 0xFFFF);

    int16_t xorResult[4];
    for (int i = 0; i < 4; i++) {
        xorResult[i] = partIV[i] ^ partKey[i];
    }

    int64_t result = ((int64_t)xorResult[0] << 48) |
                     ((int64_t)xorResult[1] << 32) |
                     ((int64_t)xorResult[2] << 16) |
                     (xorResult[3] & 0xFFFFL);

    int64_t value = result ^ response;
    Hooks::doVerify(value);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_v(JNIEnv *env, jclass clazz, jstring udid) {
    GameData::loginUdid = jstringToString(env,udid);
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_w(JNIEnv *env, jclass clazz, jstring macaddr) {
    GameData::macAddr = jstringToString(env,macaddr);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_helper_creeperbox_clients_CreeperBox_x(JNIEnv *env, jclass clazz) {
    return GameData::initSuccess;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_helper_creeperbox_clients_CreeperBox_y(JNIEnv *env, jclass clazz) {
    return GameData::verifyTime;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_z(JNIEnv *env, jclass clazz, jboolean crash) {
    GameData::doCrash = crash;
}
extern "C"
JNIEXPORT void JNICALL
Java_helper_creeperbox_clients_CreeperBox_a1(JNIEnv *env, jclass clazz, jint type) {
    GameData::skinType = type;
}