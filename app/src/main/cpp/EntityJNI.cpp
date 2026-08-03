#include "JNIHelpers.h"
#include "SDK/Actor.h"
#include "Memory/GameData.h"

// EntityLocalPlayer Native Methods
void LocalPlayer_attack(JNIEnv *env, jobject thiz, jobject other) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->attack(getInstance<Actor*>(env,other));
}

void LocalPlayer_applyTurnDelta(JNIEnv *env, jobject thiz, jobject delta) {
    Vec2* d = new Vec2(toVec2(env,delta));
    getInstance<LocalPlayer*>(env,thiz)->applyTurnDelta(d);
    delete d;
}

jobject LocalPlayer_getHitResultStart(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->start);
}

jobject LocalPlayer_getHitResultEnd(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->end);
}

jint LocalPlayer_getHitResultType(JNIEnv *env, jobject thiz) {
    return (int)getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitType;
}

jbyte LocalPlayer_getHitResultFace(JNIEnv *env, jobject thiz) {
    return (jbyte)getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->face;
}

jobject LocalPlayer_getHitResultBlock(JNIEnv *env, jobject thiz) {
    return fromVec3i(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitBlock);
}

jobject LocalPlayer_getHitResultPos(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitPos);
}

void LocalPlayer_setHitResultStart(JNIEnv *env, jobject thiz, jobject vec) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->start = toVec3(env,vec);
}

void LocalPlayer_setHitResultEnd(JNIEnv *env, jobject thiz, jobject vec) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->end = toVec3(env,vec);
}

void LocalPlayer_setHitResultType(JNIEnv *env, jobject thiz, jint type) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitType = static_cast<HitType>(type);
}

void LocalPlayer_setHitResultFace(JNIEnv *env, jobject thiz, jbyte hit_face) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->face = hit_face;
}

void LocalPlayer_setHitResultBlock(JNIEnv *env, jobject thiz, jobject pos) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitBlock = toVec3i(env,pos);
}

void LocalPlayer_setHitResultPos(JNIEnv *env, jobject thiz, jobject pos) {
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getHitResult()->hitPos = toVec3(env,pos);
}

jobject LocalPlayer_getMoveInput(JNIEnv *env, jobject thiz) {
    jclass cls = env->FindClass("helper/creeperbox/sdk/component/MoveInputComponent");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls, "<init>", "()V"));
    MoveInputComponent* input = getInstance<LocalPlayer*>(env,thiz)->getMoveInputHandler();

    jfieldID isSneakDownID = env->GetFieldID(cls, "isSneakDown", "Z");
    env->SetBooleanField(obj,isSneakDownID,input->isSneakDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isSneakToggleDown", "Z"), input->isSneakToggleDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isFlyDownSlowDown", "Z"), input->isFlyDownSlowDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isFlyUpSlowDown", "Z"), input->isFlyUpSlowDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isAscendScaffoldingDown", "Z"), input->isAscendScaffoldingDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isDescendScaffoldingDown", "Z"), input->isDescendScaffoldingDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isJumpDown", "Z"), input->isJumpDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isSprintDown", "Z"), input->isSprintDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isUpLeftDown", "Z"), input->isUpLeftDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isUpRightDown", "Z"), input->isUpRightDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isUpDown", "Z"), input->isUpDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isDownDown", "Z"), input->isDownDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isLeftDown", "Z"), input->isLeftDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isRightDown", "Z"), input->isRightDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isFlyingAscendDown", "Z"), input->isFlyingAscendDown);
    env->SetBooleanField(obj, env->GetFieldID(cls, "isFlyingDescendDown", "Z"), input->isFlyingDescendDown);
    env->SetFloatField(obj, env->GetFieldID(cls, "moveSide", "F"), input->moveSide);
    env->SetFloatField(obj, env->GetFieldID(cls, "moveForward", "F"), input->moveForward);

    return obj;
}

void LocalPlayer_destroyBlock(JNIEnv *env, jobject thiz, jobject pos, jint enum_facing_index) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->destroyBlock(toVec3i(env,pos),enum_facing_index);
}

jboolean LocalPlayer_startDestroyBlock(JNIEnv *env, jobject thiz, jobject pos, jint enum_facing_index) {
    bool ret = true;
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->startDestroyBlock(toVec3i(env,pos),enum_facing_index,ret);
    return ret;
}

jboolean LocalPlayer_continueDestroyBlock(JNIEnv *env, jobject thiz, jobject pos, jint enum_facing_index) {
    bool ret = true;
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->continueDestroyBlock(toVec3i(env,pos),enum_facing_index,ret);
    return ret;
}

void LocalPlayer_startBuildBlock(JNIEnv *env, jobject thiz, jobject pos, jint enum_facing_index) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->startBuildBlock(toVec3i(env,pos),enum_facing_index);
}

void LocalPlayer_buildBlock(JNIEnv *env, jobject thiz, jobject pos, jint enum_facing_index, jboolean check_item) {
    Vec3i* vec = new Vec3i(toVec3i(env,pos));
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->buildBlock(vec,enum_facing_index,check_item);
    delete vec;
}

void LocalPlayer_continueBuildBlock(JNIEnv *env, jobject thiz, jobject pos, jint enum_facing_index) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->continueBuildBlock(toVec3i(env,pos),enum_facing_index);
}

void LocalPlayer_stopBuildBlock(JNIEnv *env, jobject thiz) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->stopBuildBlock();
}

void LocalPlayer_sendPacket(JNIEnv *env, jobject thiz, jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    CustomWritePacket* packet = new CustomWritePacket(s);
    packet->callEvent = false;
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getPacketSender()->sendToServer(packet);
    env->DeleteLocalRef(data);
    delete packet;
}

void LocalPlayer_sendPacketEvent(JNIEnv *env, jobject thiz, jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    CustomWritePacket* packet = new CustomWritePacket(s);
    packet->callEvent = true;
    getInstance<LocalPlayer*>(env,thiz)->getLevel()->getPacketSender()->sendToServer(packet);
    env->DeleteLocalRef(data);
    delete packet;
}

void LocalPlayer_pushPacket(JNIEnv *env, jobject thiz, jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    GameData::packetList.push_back(new CustomReadPacket(s, true));
    env->DeleteLocalRef(data);
}

void LocalPlayer_pushPacket2(JNIEnv *env, jobject thiz, jbyteArray data) {
    std::string* s = new std::string();
    convertByteArrayToString(env,data,s);
    GameData::packetList.push_back(new CustomReadPacket(s, false));
    env->DeleteLocalRef(data);
}

jobject LocalPlayer_getInventory(JNIEnv *env, jobject thiz) {
    PlayerInventory* inventory = getInstance<LocalPlayer*>(env,thiz)->getInventory();
    jclass cls = env->FindClass("helper/creeperbox/sdk/inventory/PlayerInventory");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(inventory));
    return obj;
}

void LocalPlayer_displayClientMessage(JNIEnv *env, jobject thiz, jstring msg) {
    getInstance<LocalPlayer*>(env,thiz)->displayClientMessage(jstringToString(env,msg));
}

void LocalPlayer_setNoSlow(JNIEnv *env, jclass clazz, jboolean no_slow) {
    GameData::doNoSlow = no_slow;
}

void LocalPlayer_setInvMove(JNIEnv *env, jclass clazz, jboolean inv_move) {
    GameData::doInvMove = inv_move;
}

jfloat LocalPlayer_getDestroyRate(JNIEnv *env, jobject thiz, jobject block) {
    return getInstance<LocalPlayer *>(env, thiz)->getDestroyRate(getInstance<Block*>(env, block));
}

void LocalPlayer_setTimer(JNIEnv *env, jclass clazz, jfloat timer) {
    if(GameData::clientInstance != nullptr){
        GameData::clientInstance->minecraft->setTimer(timer);
    }
}

// EntityLocalPlayer Static Methods
void EntityLocalPlayer_setAttackRange(JNIEnv *env, jclass clazz, jfloat range) {
    GameData::reach = range;
}

void EntityLocalPlayer_setBuildRange(JNIEnv *env, jclass clazz, jfloat range) {
    GameData::buildReach = range;
}

void EntityLocalPlayer_setForcePos(JNIEnv *env, jclass clazz, jboolean force) {
    GameData::forcePos = force;
}

jint EntityLocalPlayer_getView(JNIEnv *env, jclass clazz) {
    if(GameData::clientInstance == nullptr) return 0;
    return GameData::clientInstance->getOptions()->getPlayerViewPerspective();
}

void LocalPlayer_stopDestroyBlock(JNIEnv *env, jobject thiz, jobject pos) {
    getInstance<LocalPlayer*>(env,thiz)->getGameMode()->stopDestroyBlock(toVec3i(env,pos));
}

void LocalPlayer_setSwingTick(JNIEnv *env, jobject thiz, jint tick) {
    getInstance<LocalPlayer*>(env,thiz)->swingTick = tick;
}

jint LocalPlayer_getSwingTick(JNIEnv *env, jobject thiz) {
    return getInstance<LocalPlayer*>(env,thiz)->swingTick;
}

void EntityLocalPlayer_setItemNoRot(JNIEnv *env, jclass clazz, jboolean item_no_rot) {
    GameData::doItemNoRot = item_no_rot;
}

void EntityLocalPlayer_setNoWeb(JNIEnv *env, jclass clazz, jboolean noweb) {
    GameData::doNoWeb = noweb;
}

void LocalPlayer_setJumpDelay(JNIEnv *env, jobject thiz, jint delay) {
    getInstance<LocalPlayer*>(env,thiz)->getMobJumpComponent()->jumpDelay = delay;
}

// EntityPlayer Methods
jobject EntityPlayer_getSkin(JNIEnv *env, jobject thiz) {
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

static const JNINativeMethod gEntityLocalPlayerMethods[] = {
        {"a", "(Lhelper/creeperbox/sdk/entity/type/EntityActor;)V", (void*)LocalPlayer_attack},
        {"k", "(Lhelper/creeperbox/sdk/math/Vec2f;)V", (void*)LocalPlayer_applyTurnDelta},
        {"l3", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)LocalPlayer_getHitResultStart},
        {"m", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)LocalPlayer_getHitResultEnd},
        {"n3", "()I", (void*)LocalPlayer_getHitResultType},
        {"o", "()B", (void*)LocalPlayer_getHitResultFace},
        {"p3", "()Lhelper/creeperbox/sdk/math/Vec3i;", (void*)LocalPlayer_getHitResultBlock},
        {"q", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)LocalPlayer_getHitResultPos},
        {"r", "(Lhelper/creeperbox/sdk/math/Vec3f;)V", (void*)LocalPlayer_setHitResultStart},
        {"s", "(Lhelper/creeperbox/sdk/math/Vec3f;)V", (void*)LocalPlayer_setHitResultEnd},
        {"t", "(I)V", (void*)LocalPlayer_setHitResultType},
        {"u", "(B)V", (void*)LocalPlayer_setHitResultFace},
        {"v", "(Lhelper/creeperbox/sdk/math/Vec3i;)V", (void*)LocalPlayer_setHitResultBlock},
        {"w", "(Lhelper/creeperbox/sdk/math/Vec3f;)V", (void*)LocalPlayer_setHitResultPos},
        {"j2", "()Lhelper/creeperbox/sdk/component/MoveInputComponent;", (void*)LocalPlayer_getMoveInput},
        {"b", "(Lhelper/creeperbox/sdk/math/Vec3i;I)V", (void*)LocalPlayer_destroyBlock},
        {"c", "(Lhelper/creeperbox/sdk/math/Vec3i;I)Z", (void*)LocalPlayer_startDestroyBlock},
        {"d", "(Lhelper/creeperbox/sdk/math/Vec3i;I)Z", (void*)LocalPlayer_continueDestroyBlock},
        {"f", "(Lhelper/creeperbox/sdk/math/Vec3i;I)V", (void*)LocalPlayer_startBuildBlock},
        {"g", "(Lhelper/creeperbox/sdk/math/Vec3i;IZ)V", (void*)LocalPlayer_buildBlock},
        {"h", "(Lhelper/creeperbox/sdk/math/Vec3i;I)V", (void*)LocalPlayer_continueBuildBlock},
        {"i1", "()V", (void*)LocalPlayer_stopBuildBlock},
        {"a1", "([B)V", (void*)LocalPlayer_sendPacket},
        {"x", "([B)V", (void*)LocalPlayer_sendPacketEvent},
        {"z", "([B)V", (void*)LocalPlayer_pushPacket},
        {"y", "([B)V", (void*)LocalPlayer_pushPacket2},
        {"g3", "()Lhelper/creeperbox/sdk/inventory/PlayerInventory;", (void*)LocalPlayer_getInventory},
        {"b1", "(Ljava/lang/String;)V", (void*)LocalPlayer_displayClientMessage},
        {"c1", "(Z)V", (void*)LocalPlayer_setNoSlow},
        {"d1", "(Z)V", (void*)LocalPlayer_setInvMove},
        {"e1", "(Lhelper/creeperbox/sdk/block/Block;)F", (void*)LocalPlayer_getDestroyRate},
        {"f1", "(F)V", (void*)LocalPlayer_setTimer},
        {"f4", "(F)V", (void*)EntityLocalPlayer_setAttackRange},
        {"f5", "(F)V", (void*)EntityLocalPlayer_setBuildRange},
        {"f6", "(Z)V", (void*)EntityLocalPlayer_setForcePos},
        {"f7", "()I", (void*)EntityLocalPlayer_getView},
        {"e2", "(Lhelper/creeperbox/sdk/math/Vec3i;)V", (void*)LocalPlayer_stopDestroyBlock},
        {"g1", "(I)V", (void*)LocalPlayer_setSwingTick},
        {"h1", "()I", (void*)LocalPlayer_getSwingTick},
        {"i2", "(Z)V", (void*)EntityLocalPlayer_setItemNoRot},
        {"j4", "(Z)V", (void*)EntityLocalPlayer_setNoWeb},
        {"j5", "(I)V", (void*)LocalPlayer_setJumpDelay}
};

static const JNINativeMethod gEntityPlayerMethods[] = {
        {"ab", "()Lorg/cloudburstmc/protocol/bedrock/data/skin/SerializedSkin;", (void*)EntityPlayer_getSkin}
};


void register_Entity(JNIEnv* env) {
    jclass localPlayerClass = env->FindClass("helper/creeperbox/sdk/entity/type/EntityLocalPlayer");
    if(localPlayerClass) env->RegisterNatives(localPlayerClass, gEntityLocalPlayerMethods, sizeof(gEntityLocalPlayerMethods) / sizeof(gEntityLocalPlayerMethods[0]));

    jclass playerClass = env->FindClass("helper/creeperbox/sdk/entity/type/EntityPlayer");
    if(playerClass) env->RegisterNatives(playerClass, gEntityPlayerMethods, sizeof(gEntityPlayerMethods) / sizeof(gEntityPlayerMethods[0]));
}
