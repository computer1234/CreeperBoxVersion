#include "JNIHelpers.h"
#include "SDK/MobileClient.h"
#include "Verify/Main.h"
#include "Include/Arm64InlineHook/arm64_inlinehook.h"
#include "Memory/GameData.h"
#include "SDK/ClientInstance.h"
#include "SDK/Render2D.h"
#include "Utils/JNIUtils.h"
#include <vector>
#include <algorithm>

// CreeperBox Native Methods
jboolean CreeperBox_runPython(JNIEnv *env, jobject thiz, jstring cmd) {
    *reinterpret_cast<bool*>(GameData::base+0xFF97600) = false;
    using ensureGIL = void* (__fastcall*)();
    void* gState = reinterpret_cast<ensureGIL>(GameData::base+0xEAD5DA0)();
    using pyAddModule = void* (__fastcall*)(const char*);
    void* _module = reinterpret_cast<pyAddModule>(GameData::base+0xEA55A74)("__main__");
    using pyGetDict = void* (__fastcall*)(void*);
    void* dist = reinterpret_cast<pyGetDict>(GameData::base+0xE9CA87C)(_module);
    const char *command = env->GetStringUTFChars(cmd,NULL);
    using pyRun = int (__fastcall*)(const char *code,int start,void*,void*,void*);
    int result = reinterpret_cast<pyRun>(GameData::base+0xEAD76BC)(command,0x101,dist,dist,NULL);
    env->ReleaseStringUTFChars(cmd,command);
    using releaseEGL = void* (__fastcall*)(void*);
    reinterpret_cast<releaseEGL>(GameData::base+0xEAD5E38)(gState);
    return result != 0;
}

void CreeperBox_setCookie(JNIEnv *env, jobject thiz, jstring cookie) {
    GameData::cookie = jstringToString(env,cookie);
}

jboolean CreeperBox_getShowProgress(JNIEnv *env, jobject thiz) {
    if(GameData::clientInstance == nullptr || GameData::clientInstance->getGuiData() == nullptr) return false;
    return GameData::clientInstance->getGuiData()->showProgress;
}

void CreeperBox_handleDestroyOrAttackButtonPress(JNIEnv *env, jobject thiz) {
    if(GameData::clientInstance != nullptr)
        GameData::clientInstance->handleDestroyOrAttackButtonPress();
}

void CreeperBox_handleBuildOrInteractButtonPress(JNIEnv *env, jobject thiz) {
    if(GameData::clientInstance != nullptr)
        GameData::clientInstance->handleBuildOrInteractButtonPress();
}

jint CreeperBox_getPointerX(JNIEnv *env, jobject thiz) {
    if(GameData::clientInstance == nullptr || GameData::clientInstance->getGuiData() == nullptr) return 0;
    return GameData::clientInstance->getGuiData()->pointerX;
}

jint CreeperBox_getPointerY(JNIEnv *env, jobject thiz) {
    if(GameData::clientInstance == nullptr || GameData::clientInstance->getGuiData() == nullptr) return 0;
    return GameData::clientInstance->getGuiData()->pointerY;
}

jstring CreeperBox_getIP(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(GameData::lastIP.c_str());
}

jstring CreeperBox_getRoomSid(JNIEnv *env, jobject thiz) {
    if(GameData::base == 0) return env->NewStringUTF("");
    MobileClient* mobile = MobileClient::singleton();
    if(mobile == nullptr) return env->NewStringUTF("");
    return env->NewStringUTF(mobile->mRoomSid.c_str());
}

void CreeperBox_setRoomSid(JNIEnv *env, jobject thiz, jstring sid) {
    if(GameData::base == 0) return;
    MobileClient* mobile = MobileClient::singleton();
    if(mobile != nullptr)
        mobile->mRoomSid = jstringToString(env,sid);
}

void CreeperBox_setNextIP(JNIEnv *env, jobject thiz, jstring ip) {
    GameData::nextIP = jstringToString(env,ip);
}

void CreeperBox_setNextPort(JNIEnv *env, jobject thiz, jint port) {
    GameData::nextPort = port;
}

jint CreeperBox_getNextRequestID(JNIEnv *env, jclass clazz) {
    if(GameData::base == 0) return 0;
    return *reinterpret_cast<int*>(GameData::base+0xFE68D58);
}

void CreeperBox_setNextRequestID(JNIEnv *env, jclass clazz, jint id) {
    if(GameData::base == 0) return;
    *reinterpret_cast<int*>(GameData::base+0xFE68D58) = id;
}

void CreeperBox_setXRay(JNIEnv *env, jclass clazz, jboolean do_xray) {
    GameData::doXRay = do_xray;
}

void CreeperBox_setChestXRay(JNIEnv *env, jclass clazz, jboolean do_xray) {
    GameData::doChestXray = do_xray;
}

void CreeperBox_addXRayBlock(JNIEnv *env, jclass clazz, jstring name) {
    GameData::xRayList.push_back(jstringToString(env,name));
}

void CreeperBox_removeXRayBlock(JNIEnv *env, jclass clazz, jstring name) {
    std::string n = jstringToString(env,name);
    GameData::xRayList.erase(std::remove(GameData::xRayList.begin(), GameData::xRayList.end(), n), GameData::xRayList.end());
}

jint CreeperBox_getFPS(JNIEnv *env, jclass clazz) {
    if(GameData::base == 0) return 0;
    MobileClient* mobile = MobileClient::singleton();
    if(mobile == nullptr) return 0;
    return (int)mobile->mFps;
}

void CreeperBox_setGamma(JNIEnv *env, jclass clazz, jfloat gamma) {
    GameData::gamma = gamma;
}

void CreeperBox_resetHooks(JNIEnv *env, jclass clazz) {
    Arm64InlineHook* hooker = Arm64InlineHook::getInstance();
    hooker->reset();
}

void CreeperBox_setLoginUdid(JNIEnv *env, jclass clazz, jstring udid) {
    GameData::loginUdid = jstringToString(env,udid);
}

void CreeperBox_setMacAddr(JNIEnv *env, jclass clazz, jstring macaddr) {
    GameData::macAddr = jstringToString(env,macaddr);
}

jboolean CreeperBox_initSuccess(JNIEnv *env, jclass clazz) {
    return GameData::initSuccess;
}

jlong CreeperBox_getVerifyTime(JNIEnv *env, jclass clazz) {
    return GameData::verifyTime;
}

void CreeperBox_setDoCrash(JNIEnv *env, jclass clazz, jboolean crash) {
    GameData::doCrash = crash;
}

void CreeperBox_setSkinType(JNIEnv *env, jclass clazz, jint type) {
    GameData::skinType = type;
}

jobject CreeperBox_getLocalPlayer(JNIEnv *env, jobject thiz) {
    if(GameData::clientInstance == nullptr){
        return nullptr;
    }
    LocalPlayer* player = GameData::clientInstance->getLocalPlayer();
    if(player == nullptr){
        return nullptr;
    }
    jclass cls = env->FindClass("helper/creeperbox/sdk/entity/type/EntityLocalPlayer");
    if(cls == nullptr) return nullptr;
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(player));
    return obj;
}

// ========== 验证接口 ==========
// 外部声明 get_device_info 函数
extern void get_device_info(char *output_hash);

// 传入卡密信息，启动验证流程，等待握手完成
// 返回 true 表示验证成功，false 表示失败
jboolean CreeperBox_verify(JNIEnv *env, jclass clazz, jstring username, jstring password, jstring hwid) {
    using namespace team::cool::client;
    
    // 设置凭据
    std::string user = jstringToString(env, username);
    std::string pass = jstringToString(env, password);
    
    // 在 C++ 内部获取设备 HWID，忽略 Java 传入的参数
    char hwid_buffer[17] = {0};
    get_device_info(hwid_buffer);
    std::string hw(hwid_buffer);
    
    Main::setCredentials(user, pass, hw);
    
    // 启动验证流程
    Main::startVerify();
    
    // 等待验证完成（30秒超时）
    bool success = Main::waitForVerification(30000, 500);
    
    return success ? JNI_TRUE : JNI_FALSE;
}

// 获取验证状态字符串
jstring CreeperBox_getVerifyStatus(JNIEnv *env, jclass clazz) {
    using namespace team::cool::client;
    return env->NewStringUTF(Main::getStatusString().c_str());
}

// 获取验证错误信息
jstring CreeperBox_getVerifyError(JNIEnv *env, jclass clazz) {
    using namespace team::cool::client;
    return env->NewStringUTF(Main::getLastError().c_str());
}

// Native Method Registration
static const JNINativeMethod gCreeperBoxMethods[] = {
        {"getLocalPlayer", "()Lhelper/creeperbox/sdk/entity/type/EntityLocalPlayer;", (void*)CreeperBox_getLocalPlayer},
        {"a", "(Ljava/lang/String;)Z", (void*)CreeperBox_runPython},
        {"b", "(Ljava/lang/String;)V", (void*)CreeperBox_setCookie},
        {"c", "()Z", (void*)CreeperBox_getShowProgress},
        {"d", "()V", (void*)CreeperBox_handleDestroyOrAttackButtonPress},
        {"e", "()V", (void*)CreeperBox_handleBuildOrInteractButtonPress},
        {"f", "()I", (void*)CreeperBox_getPointerX},
        {"g", "()I", (void*)CreeperBox_getPointerY},
        {"h", "()Ljava/lang/String;", (void*)CreeperBox_getIP},
        {"i", "()Ljava/lang/String;", (void*)CreeperBox_getRoomSid},
        {"j", "(Ljava/lang/String;)V", (void*)CreeperBox_setRoomSid},
        {"k", "(Ljava/lang/String;)V", (void*)CreeperBox_setNextIP},
        {"l", "(I)V", (void*)CreeperBox_setNextPort},
        {"m", "()I", (void*)CreeperBox_getNextRequestID},
        {"n", "(I)V", (void*)CreeperBox_setNextRequestID},
        {"o", "(Z)V", (void*)CreeperBox_setXRay},
        {"p", "(Z)V", (void*)CreeperBox_setChestXRay},
        {"q", "(Ljava/lang/String;)V", (void*)CreeperBox_addXRayBlock},
        {"r", "(Ljava/lang/String;)V", (void*)CreeperBox_removeXRayBlock},
        {"s", "()I", (void*)CreeperBox_getFPS},
        {"t", "(F)V", (void*)CreeperBox_setGamma},
        {"u", "()V", (void*)CreeperBox_resetHooks},
        {"v", "(Ljava/lang/String;)V", (void*)CreeperBox_setLoginUdid},
        {"w", "(Ljava/lang/String;)V", (void*)CreeperBox_setMacAddr},
        {"x", "()Z", (void*)CreeperBox_initSuccess},
        {"y", "()J", (void*)CreeperBox_getVerifyTime},
        {"z", "(Z)V", (void*)CreeperBox_setDoCrash},
        {"a1", "(I)V", (void*)CreeperBox_setSkinType},
        // 验证接口
        {"b1", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z", (void*)CreeperBox_verify},
        {"c1", "()Ljava/lang/String;", (void*)CreeperBox_getVerifyStatus},
        {"d1", "()Ljava/lang/String;", (void*)CreeperBox_getVerifyError}
};

void register_CreeperBox(JNIEnv* env) {
    jclass clazz = env->FindClass("helper/creeperbox/clients/CreeperBox");
    if(clazz) env->RegisterNatives(clazz, gCreeperBoxMethods, sizeof(gCreeperBoxMethods) / sizeof(gCreeperBoxMethods[0]));
}
