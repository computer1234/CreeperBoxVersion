#include "JNIHelpers.h"

jint EntityActor_getEntityTypeId(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getEntityTypeId();
}

jboolean EntityActor_isAlive(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isAlive();
}

void EntityActor_swing(JNIEnv *env, jobject thiz) {
    getInstance<Actor*>(env,thiz)->swing();
}

void EntityActor_setSprinting(JNIEnv *env, jobject thiz, jboolean is_sprinting) {
    getInstance<Actor*>(env,thiz)->setSprinting(is_sprinting);
}

jboolean EntityActor_isClientSide(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isClientSide();
}

jboolean EntityActor_isInvisible(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isInvisible();
}

jboolean EntityActor_canSeeActor(JNIEnv *env, jobject thiz, jobject other) {
    return getInstance<Actor*>(env,thiz)->canSeeActor(getInstance<Actor*>(env,other));
}

jstring EntityActor_getNameTag(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<Actor*>(env,thiz)->getNameTag()->c_str());
}

jstring EntityActor_getNamespace(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<Actor*>(env,thiz)->getNamespace().c_str());
}

jboolean EntityActor_isOnGround(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isOnGround();
}

void EntityActor_setOnGround(JNIEnv *env, jobject thiz, jboolean on_ground) {
    getInstance<Actor*>(env,thiz)->setOnGround(on_ground);
}

void EntityActor_setPos(JNIEnv *env, jobject thiz, jobject va) {
    Vec3* vec3 = new Vec3(toVec3(env,va));
    getInstance<Actor*>(env,thiz)->setPos(vec3);
    delete vec3;
}

jobject EntityActor_getPos(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getPos());
}

jfloat EntityActor_getFallDistance(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getFallDistanceComponent()->fallDistance;
}

void EntityActor_setFallDistance(JNIEnv *env, jobject thiz, jfloat value) {
    getInstance<Actor*>(env,thiz)->getFallDistanceComponent()->fallDistance = value;
}

jlong EntityActor_getRuntimeID(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getRuntimeIDComponent()->runtimeID;
}

void EntityActor_setRuntimeID(JNIEnv *env, jobject thiz, jlong value) {
    getInstance<Actor*>(env,thiz)->getRuntimeIDComponent()->runtimeID = value;
}

jint EntityActor_getGameType(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorGameTypeComponent()->gameType;
}

void EntityActor_setGameType(JNIEnv *env, jobject thiz, jint value) {
    getInstance<Actor*>(env,thiz)->getActorGameTypeComponent()->gameType = value;
}

jobject EntityActor_getBlockMovementSlowdownMultiplier(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getBlockMovementSlowdownMultiplierComponent()->slowDownFactor);
}

void EntityActor_setBlockMovementSlowdownMultiplier(JNIEnv *env, jobject thiz, jobject value) {
    getInstance<Actor*>(env,thiz)->getBlockMovementSlowdownMultiplierComponent()->slowDownFactor = toVec3(env,value);
}

jfloat EntityActor_getMaxAutoStep(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getMaxAutoStepComponent()->stepHeight;
}

void EntityActor_setMaxAutoStep(JNIEnv *env, jobject thiz, jfloat value) {
    getInstance<Actor*>(env,thiz)->getMaxAutoStepComponent()->stepHeight = value;
}

jlong EntityActor_getUniqueID(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorUniqueIDComponent()->uniqueID;
}

void EntityActor_setUniqueID(JNIEnv *env, jobject thiz, jlong value) {
    getInstance<Actor*>(env,thiz)->getActorUniqueIDComponent()->uniqueID = value;
}

jboolean EntityActor_getAbilityBool(JNIEnv *env, jobject thiz, jint index) {
    return getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->getAbilityBool(index);
}

jfloat EntityActor_getAbilityFloat(JNIEnv *env, jobject thiz, jint index) {
    return getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->getAbilityFloat(index);
}

void EntityActor_setAbilityBool(JNIEnv *env, jobject thiz, jint index, jboolean value) {
    getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->setAbilityBool(index,value);
}

void EntityActor_setAbilityFloat(JNIEnv *env, jobject thiz, jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAbilitiesComponent()->setAbilityFloat(index,value);
}

jfloat EntityActor_getActorHeadRotationYaw(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yaw;
}

jfloat EntityActor_getActorHeadRotationYawPrev(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yawPrev;
}

void EntityActor_setActorHeadRotationYaw(JNIEnv *env, jobject thiz, jfloat value) {
    getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yaw = value;
}

void EntityActor_setActorHeadRotationYawPrev(JNIEnv *env, jobject thiz, jfloat value) {
    getInstance<Actor*>(env,thiz)->getActorHeadRotationComponent()->yawPrev = value;
}

jfloat EntityActor_getMobBodyRotationYaw(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yaw;
}

jfloat EntityActor_getMobBodyRotationYawPrev(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yawPrev;
}

void EntityActor_setMobBodyRotationYaw(JNIEnv *env, jobject thiz, jfloat value) {
    getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yaw = value;
}

void EntityActor_setMobBodyRotationYawPrev(JNIEnv *env, jobject thiz, jfloat value) {
    getInstance<Actor*>(env,thiz)->getMobBodyRotationComponent()->yawPrev = value;
}

jobject EntityActor_getRotation(JNIEnv *env, jobject thiz) {
    return fromVec2(env,getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRot);
}

jobject EntityActor_getRotationPrev(JNIEnv *env, jobject thiz) {
    return fromVec2(env,getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRotPrev);
}

void EntityActor_setRotation(JNIEnv *env, jobject thiz, jobject value) {
    getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRot = toVec2(env,value);
}

void EntityActor_setRotationPrev(JNIEnv *env, jobject thiz, jobject value) {
    getInstance<Actor*>(env,thiz)->getActorRotationComponent()->headRotPrev = toVec2(env,value);
}

jobject EntityActor_getPosPrev(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getPosPrev());
}

void EntityActor_setPosPrev(JNIEnv *env, jobject thiz, jobject value) {
    getInstance<Actor*>(env,thiz)->getStateVectorComponent()->posPrev = toVec3(env,value);
}

jobject EntityActor_getMotion(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<Actor*>(env,thiz)->getMotion());
}

void EntityActor_setMotion(JNIEnv *env, jobject thiz, jobject value) {
    getInstance<Actor*>(env,thiz)->setMotion(toVec3(env,value));
}

void EntityActor_setAABB(JNIEnv *env, jobject thiz, jobject aabb) {
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

jobject EntityActor_getAABB(JNIEnv *env, jobject thiz) {
    return fromAABB(env,*getInstance<Actor*>(env,thiz)->getAABB());
}

void EntityActor_setHitBoxSize(JNIEnv *env, jobject thiz, jobject size) {
    getInstance<Actor*>(env,thiz)->setSize(toVec2(env,size));
}

jobject EntityActor_getHitBoxSize(JNIEnv *env, jobject thiz) {
    return fromVec2(env,getInstance<Actor*>(env,thiz)->getSize());
}

jboolean EntityActor_getStatusFlag(JNIEnv *env, jobject thiz, jint index) {
    return getInstance<Actor*>(env,thiz)->getStatusFlag(index);
}

void EntityActor_setStatusFlag(JNIEnv *env, jobject thiz, jint index, jboolean flag) {
    getInstance<Actor*>(env,thiz)->setStatusFlag(index,flag);
}

jfloat EntityActor_getAttributeCurrentValue(JNIEnv *env, jobject thiz, jint index) {
    return getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentValue;
}

jfloat EntityActor_getAttributeMinValue(JNIEnv *env, jobject thiz, jint index) {
    return getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMinValue;
}

jfloat EntityActor_getAttributeMaxValue(JNIEnv *env, jobject thiz, jint index) {
    return getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMaxValue;
}

void EntityActor_setAttributeCurrentValue(JNIEnv *env, jobject thiz, jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentValue = value;
}

void EntityActor_setAttributeMinValue(JNIEnv *env, jobject thiz, jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMinValue = value;
}

void EntityActor_setAttributeMaxValue(JNIEnv *env, jobject thiz, jint index, jfloat value) {
    getInstance<Actor*>(env,thiz)->getAttribute(index)->mCurrentMaxValue = value;
}

jboolean EntityActor_isSneaking(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getStatusFlag(1);
}

jboolean EntityActor_isSprintingStatus(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->getStatusFlag(3);
}

void EntityActor_setSneaking(JNIEnv *env, jobject thiz, jboolean sneaking) {
    getInstance<Actor*>(env,thiz)->setSneaking(sneaking);
}

jboolean EntityActor_isPrimaryLocalPlayer(JNIEnv *env, jobject thiz) {
    return getInstance<Actor*>(env,thiz)->isPrimaryLocalPlayer();
}

jobject EntityActor_getVehicle(JNIEnv *env, jobject thiz) {
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

jobject EntityActor_getLevel(JNIEnv *env, jobject thiz) {
    Level* level = getInstance<Actor*>(env,thiz)->getLevel();
    jclass cls = env->FindClass("helper/creeperbox/sdk/level/Level");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(level));
    return obj;
}

jobject EntityActor_getItemInHand(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<Actor*>(env,thiz)->getItemInHand();
    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(item));
    return obj;
}

jobject EntityActor_getItemOffHand(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<Actor*>(env,thiz)->getItemOffHand();
    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(item));
    return obj;
}

jobject EntityActor_getArmor(JNIEnv *env, jobject thiz, jint slot) {
    ItemStack* item = getInstance<Actor*>(env,thiz)->getArmor(slot);
    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(item));
    return obj;
}

jobjectArray EntityActor_getEffects(JNIEnv *env, jobject thiz) {
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

static const JNINativeMethod gEntityActorMethods[] = {
        {"a", "()I", (void*)EntityActor_getEntityTypeId},
        {"b", "()Z", (void*)EntityActor_isAlive},
        {"c", "()V", (void*)EntityActor_swing},
        {"d", "(Z)V", (void*)EntityActor_setSprinting},
        {"e", "()Z", (void*)EntityActor_isClientSide},
        {"f", "()Z", (void*)EntityActor_isInvisible},
        {"g", "(Lhelper/creeperbox/sdk/entity/type/EntityActor;)Z", (void*)EntityActor_canSeeActor},
        {"h", "()Ljava/lang/String;", (void*)EntityActor_getNameTag},
        {"i", "()Ljava/lang/String;", (void*)EntityActor_getNamespace},
        {"j", "()Z", (void*)EntityActor_isOnGround},
        {"k", "(Z)V", (void*)EntityActor_setOnGround},
        {"l", "()F", (void*)EntityActor_getFallDistance},
        {"m", "(F)V", (void*)EntityActor_setFallDistance},
        {"n", "()J", (void*)EntityActor_getRuntimeID},
        {"o", "(J)V", (void*)EntityActor_setRuntimeID},
        {"p", "()I", (void*)EntityActor_getGameType},
        {"q", "(I)V", (void*)EntityActor_setGameType},
        {"r", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)EntityActor_getBlockMovementSlowdownMultiplier},
        {"s", "(Lhelper/creeperbox/sdk/math/Vec3f;)V", (void*)EntityActor_setBlockMovementSlowdownMultiplier},
        {"t", "()F", (void*)EntityActor_getMaxAutoStep},
        {"u", "(F)V", (void*)EntityActor_setMaxAutoStep},
        {"v", "()J", (void*)EntityActor_getUniqueID},
        {"w", "(J)V", (void*)EntityActor_setUniqueID},
        {"x", "(I)Z", (void*)EntityActor_getAbilityBool},
        {"y", "(I)F", (void*)EntityActor_getAbilityFloat},
        {"z", "(IZ)V", (void*)EntityActor_setAbilityBool},
        {"a1", "(IF)V", (void*)EntityActor_setAbilityFloat},
        {"b1", "()F", (void*)EntityActor_getActorHeadRotationYaw},
        {"c1", "()F", (void*)EntityActor_getActorHeadRotationYawPrev},
        {"d1", "(F)V", (void*)EntityActor_setActorHeadRotationYaw},
        {"e1", "(F)V", (void*)EntityActor_setActorHeadRotationYawPrev},
        {"f1", "()F", (void*)EntityActor_getMobBodyRotationYaw},
        {"g1", "()F", (void*)EntityActor_getMobBodyRotationYawPrev},
        {"h1", "(F)V", (void*)EntityActor_setMobBodyRotationYaw},
        {"i1", "(F)V", (void*)EntityActor_setMobBodyRotationYawPrev},
        {"j1", "()Lhelper/creeperbox/sdk/math/Vec2f;", (void*)EntityActor_getRotation},
        {"k1", "()Lhelper/creeperbox/sdk/math/Vec2f;", (void*)EntityActor_getRotationPrev},
        {"l1", "(Lhelper/creeperbox/sdk/math/Vec2f;)V", (void*)EntityActor_setRotation},
        {"m1", "(Lhelper/creeperbox/sdk/math/Vec2f;)V", (void*)EntityActor_setRotationPrev},
        {"n1", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)EntityActor_getPos},
        {"o1", "(Lhelper/creeperbox/sdk/math/Vec3f;)V", (void*)EntityActor_setPos},
        {"p1", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)EntityActor_getPosPrev},
        {"q1", "(Lhelper/creeperbox/sdk/math/Vec3f;)V", (void*)EntityActor_setPosPrev},
        {"r1", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)EntityActor_getMotion},
        {"s1", "(Lhelper/creeperbox/sdk/math/Vec3f;)V", (void*)EntityActor_setMotion},
        {"t1", "(Lhelper/creeperbox/sdk/math/AxisAlignedBB;)V", (void*)EntityActor_setAABB},
        {"u1", "()Lhelper/creeperbox/sdk/math/AxisAlignedBB;", (void*)EntityActor_getAABB},
        {"v1", "(Lhelper/creeperbox/sdk/math/Vec2f;)V", (void*)EntityActor_setHitBoxSize},
        {"w1", "()Lhelper/creeperbox/sdk/math/Vec2f;", (void*)EntityActor_getHitBoxSize},
        {"x1", "(I)Z", (void*)EntityActor_getStatusFlag},
        {"y1", "(IZ)V", (void*)EntityActor_setStatusFlag},
        {"z1", "(I)F", (void*)EntityActor_getAttributeCurrentValue},
        {"a2", "(I)F", (void*)EntityActor_getAttributeMinValue},
        {"b2", "(I)F", (void*)EntityActor_getAttributeMaxValue},
        {"c2", "(IF)V", (void*)EntityActor_setAttributeCurrentValue},
        {"d2", "(IF)V", (void*)EntityActor_setAttributeMinValue},
        {"e2", "(IF)V", (void*)EntityActor_setAttributeMaxValue},
        {"f2", "()Z", (void*)EntityActor_isSprintingStatus},
        {"g2", "()Z", (void*)EntityActor_isSneaking},
        {"h2", "(Z)V", (void*)EntityActor_setSneaking},
        {"i2", "()Z", (void*)EntityActor_isPrimaryLocalPlayer},
        {"k2", "()Lhelper/creeperbox/sdk/entity/type/EntityActor;", (void*)EntityActor_getVehicle},
        {"l2", "()Lhelper/creeperbox/sdk/level/Level;", (void*)EntityActor_getLevel},
        {"m2", "()Lhelper/creeperbox/sdk/item/ItemStack;", (void*)EntityActor_getItemInHand},
        {"n2", "()Lhelper/creeperbox/sdk/item/ItemStack;", (void*)EntityActor_getItemOffHand},
        {"o2", "(I)Lhelper/creeperbox/sdk/item/ItemStack;", (void*)EntityActor_getArmor},
        {"p2", "()[Lhelper/creeperbox/sdk/math/Vec3i;", (void*)EntityActor_getEffects},
};

void register_EntityActor(JNIEnv* env) {
    jclass clazz = env->FindClass("helper/creeperbox/sdk/entity/type/EntityActor");
    if (clazz == nullptr) {
        return;
    }
    env->RegisterNatives(clazz, gEntityActorMethods, sizeof(gEntityActorMethods) / sizeof(JNINativeMethod));
}
