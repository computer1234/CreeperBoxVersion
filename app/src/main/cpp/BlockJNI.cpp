#include "JNIHelpers.h"
#include "SDK/BlockSource.h"
#include "Memory/GameData.h"

// Block Native Methods
jstring Block_getNamespace(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<Block*>(env,thiz)->legacy->getNameSpace().c_str());
}

jobject Block_getCollisionShape(JNIEnv *env, jobject thiz) {
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

jint Block_getData(JNIEnv *env, jobject thiz) {
    Block* block = getInstance<Block*>(env,thiz);
    return block->mData;
}

// Material Native Methods
jboolean Material_isSolid(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mSolid;
}

jboolean Material_isBuildable(JNIEnv *env, jobject thiz) {
    return !getInstance<Material*>(env,thiz)->mNeverBuildable;
}

jboolean Material_alwaysTrue(JNIEnv *env, jobject thiz) {
    return true;
}

jboolean Material_isLiquid(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mLiquid;
}

jboolean Material_isSuperHot(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mSuperHot;
}

jboolean Material_blocksPrecipitation(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mBlocksPrecipitation;
}

jboolean Material_blocksMotion(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mBlocksMotion;
}

jfloat Material_getTranslucency(JNIEnv *env, jobject thiz) {
    return 0.0;
}

jboolean Material_isAir(JNIEnv *env, jobject thiz) {
    return getInstance<Material*>(env,thiz)->mType == Air;
}

jint Block_getRuntimeID(JNIEnv *env, jobject thiz) {
    return getInstance<Block*>(env,thiz)->runtimeID;
}

static const JNINativeMethod gBlockMethods[] = {
        {"a", "()Ljava/lang/String;", (void*)Block_getNamespace},
        {"b", "()Lhelper/creeperbox/sdk/math/AxisAlignedBB;", (void*)Block_getCollisionShape},
        {"c", "()I", (void*)Block_getRuntimeID},
        {"d", "()I", (void*)Block_getData}
};

static const JNINativeMethod gMaterialMethods[] = {
        {"a", "()Z", (void*)Material_isSolid},
        {"b", "()Z", (void*)Material_isBuildable},
        {"c", "()Z", (void*)Material_alwaysTrue},
        {"d", "()Z", (void*)Material_isLiquid},
        {"e", "()Z", (void*)Material_isSuperHot},
        {"f", "()Z", (void*)Material_blocksPrecipitation},
        {"g", "()Z", (void*)Material_blocksMotion},
        {"h", "()F", (void*)Material_getTranslucency},
        {"i", "()Z", (void*)Material_isAir}
};

void register_Block(JNIEnv* env) {
    jclass blockClass = env->FindClass("helper/creeperbox/sdk/block/Block");
    env->RegisterNatives(blockClass, gBlockMethods, sizeof(gBlockMethods) / sizeof(gBlockMethods[0]));

    jclass materialClass = env->FindClass("helper/creeperbox/sdk/block/Material");
    env->RegisterNatives(materialClass, gMaterialMethods, sizeof(gMaterialMethods) / sizeof(gMaterialMethods[0]));
}
