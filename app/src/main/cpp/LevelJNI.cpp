#include "JNIHelpers.h"
#include "SDK/BlockSource.h"
#include "Memory/GameData.h"
#include "Verify/Main.h"
#include "Verify/Guard/InlineVerify.h"

using namespace team::cool::client::guard;

// Level Native Methods
jobject Level_getBlock(JNIEnv *env, jobject thiz, jobject pos) {
    Block* block = GameData::clientInstance->getRegion()->getBlock(toVec3i(env,pos));
    jclass cls = env->FindClass("helper/creeperbox/sdk/block/Block");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(JLhelper/creeperbox/sdk/math/Vec3i;)V"),
                                 reinterpret_cast<long>(block),pos);
    return obj;
}

jobject Level_getMaterial(JNIEnv *env, jobject thiz, jobject pos) {
    Material* material = GameData::clientInstance->getRegion()->getMaterial(toVec3i(env,pos));
    jclass cls = env->FindClass("helper/creeperbox/sdk/block/Material");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(material));
    return obj;
}

jobjectArray Level_getAllPlayers(JNIEnv *env, jobject thiz) {
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

jobjectArray Level_getPlayerList(JNIEnv *env, jobject thiz) {
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

jobjectArray Level_getRuntimeEntities(JNIEnv *env, jobject thiz) {
    // Inline verification (different variant from Hooks.cpp)
    bool __entities_pass = true;
    INLINE_SECURITY_CHECK(__entities_pass = false);  // 使用完整基础版
    if (!__entities_pass) {
         // Return empty array if verification fails
         jclass cls = env->FindClass("helper/creeperbox/sdk/entity/type/EntityActor");
         return env->NewObjectArray(0, cls, nullptr);
    }
    Level* level = getInstance<Level*>(env,thiz);
    if (level == nullptr) {
        jclass cls = env->FindClass("helper/creeperbox/sdk/entity/type/EntityActor");
        return env->NewObjectArray(0, cls, nullptr);
    }

    std::vector<Actor*> list = level->getRuntimeActorList();

    level->forEachPlayer([&list](Player &player) {
        if (std::find(list.begin(), list.end(), &player) == list.end()) {
            list.push_back(&player);
        }
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
        method = env->GetStaticMethodID(genCls,"generatorEntity",
                                        "(J)Lhelper/creeperbox/sdk/entity/type/EntityActor;");
    }

    for (jsize i = 0; i < size; ++i) {
        Actor* actor = list.at(i);
        jobject javaActor = env->CallStaticObjectMethod(genCls,method,reinterpret_cast<jlong>(actor));
        env->SetObjectArrayElement(javaArray, i, javaActor);
    }

    return javaArray;
}

jlong Level_getCurrentTick(JNIEnv *env, jobject thiz) {
    return getInstance<Level*>(env,thiz)->getCurrentTick()->tick;
}

void Level_setCurrentTick(JNIEnv *env, jobject thiz, jlong tick) {
    getInstance<Level*>(env,thiz)->getCurrentTick()->tick = tick;
}

static const JNINativeMethod gLevelMethods[] = {
        {"a", "(Lhelper/creeperbox/sdk/math/Vec3i;)Lhelper/creeperbox/sdk/block/Block;", (void*)Level_getBlock},
        {"b", "(Lhelper/creeperbox/sdk/math/Vec3i;)Lhelper/creeperbox/sdk/block/Material;", (void*)Level_getMaterial},
        {"f", "()[Lhelper/creeperbox/sdk/entity/type/EntityActor;", (void*)Level_getAllPlayers},
        {"g", "()[Landroid/util/Pair;", (void*)Level_getPlayerList},
        {"c", "()[Lhelper/creeperbox/sdk/entity/type/EntityActor;", (void*)Level_getRuntimeEntities},
        {"d", "()J", (void*)Level_getCurrentTick},
        {"e", "(J)V", (void*)Level_setCurrentTick}
};

void register_Level(JNIEnv* env) {
    jclass levelClass = env->FindClass("helper/creeperbox/sdk/level/Level");
    env->RegisterNatives(levelClass, gLevelMethods, sizeof(gLevelMethods) / sizeof(gLevelMethods[0]));
}
