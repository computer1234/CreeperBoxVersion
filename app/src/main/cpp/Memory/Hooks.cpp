#include "Hooks.h"
#include "../Utils/StringUtil.h"
#include "../Utils/JNIUtils.h"
#include "../Include/Arm64InlineHook/arm64_inlinehook.h"
#include "../JavaData.h"
#include "../Verify/Main.h"
#include "../Verify/websocket/packet/s2c/S2CPacketCloudVar.h"

#include <iostream>
#include <unordered_set>
#include <sys/mman.h>
#include <unistd.h>
#include <iostream>
#include <fstream>
#include <dlfcn.h>

using CloudVar = team::cool::client::websocket::packet::s2c::S2CPacketCloudVar;

void* (*oPlayer_normalTick)(void*) = NULL;
void* (*oClientInstance_tick)(void*,void*) = NULL;
void* (*oNetworkSystem_sendInternal)(void*,void*,void*,void*) = NULL;
bool (*oNetworkConnection_receivePacket)(void*,void*,void*,void*,int) = NULL;
void* (*oFlagReceiveSide)(void*,void*,void*,void*,int) = NULL;
void* (*oScreenView_setupAndRender)(void*,void*) = NULL;
void* (*oLevelRenderer_renderLevel)(void*,void*,void*) = NULL;
void* (*oHandleNoSlow)(void*,void*,void*,void*) = NULL;
void* (*oOnInput)(void*,void*) = NULL;
void* (*oOnResetMoveInput)(void*,void*) = NULL;
void* (*oOnHandleJumpInput)(void*,void*,void*,void*,void*,void*) = NULL;
void* (*oLevelRendererPlayer_bobHurt)(void*,void*,float) = NULL;
void* (*oItemRenderer_render)(u_int64_t,u_int64_t,u_int64_t*) = NULL;
void* (*oItemInHandRenderer_renderItem)(void*,void*,void*,void*,bool ,int,bool,int) = NULL;
void* (*oParseJson)(void*,void*,void*,void*) = NULL;
std::shared_ptr<void*> (*oCRCCheck)(void*,void*,void*) = NULL;
void* (*oDoNoWeb)(void*,void*,void*,void*) = NULL;
void* (*odoGroundAndAirMoveSystem)(void*,void*,void*,void*,void*,void*,void*,void*,void*,void*,void*,void*,void*) = NULL;
float (*oGameMode_getPickRange)(void*,void*,bool) = NULL;
void* (*oSetupCamera)(void*,void*) = NULL;
int (*oGetGameType)(void*) = NULL;
int (*oShouldDisplayPlayerPosition)(void*) = NULL;
void* (*oAddChatMessage)(void*,std::string&) = NULL;
std::string (*oEasyUtil_decryptHttpContent)(const std::string&,int factor) = NULL;
std::string (*oEasyUtil_encryptHttpContent)(const std::string&) = NULL;

void* (*oPlayWorld)(void *a1, void *a2, void *a3, std::optional<RoomInfo>& roomInfo, void *a5, std::optional<SkinInfo>& a6,
          void *a7, void *a8, void *a9) = NULL;


void* (*oBlockTessellator_tessellateInWorld)(void *thiz, void *a2, Block *a3, void *a4, void *a5,
                                                 void *a6) = NULL;

void* (*oBlockTessellator_tessellateSimpleBlockInWorld)(void *thiz, void *a2, Block *a3, void *a4, void *a5) = NULL;

void* (*oBlockActorRenderDispatcher_render)(void *thiz, void *a2, void *a3, BlockActor *a4, Block *a5,
                                           void *a6, void *a7, void *a8, void *a9, void *a10,
                                           void *a11, void *a12) = NULL;

void* (*oRenderChunkBuilder_sortBlocks)(RenderChunkBuilder* thiz,void* region,void* renderChunkGeometry,void* needAirAndSimpleBlocks,void* airAndSimpleBlocks)  = NULL;

float (*oOptions_getGamma)(void*)  = NULL;
void* (*oLoginStateMachine_change)(void*,int)  = NULL;
extern "C" {
    __attribute__((visibility("hidden"))) void* (*oOnReach)() = nullptr;
}
float (*Hooks::oGameMode_getDestroyRate)(void*,void*) = NULL;

extern "C" {
    __attribute__((visibility("hidden"))) float* GameReach = &GameData::reach;
}

extern "C" void handleReach();

asm(
        ".global handleReach\n"
        "handleReach:\n"
        "    adrp x0, GameReach\n"
        "    ldr x0, [x0, :lo12:GameReach]\n"
        "    ldr s0, [x0]\n"
        "    ldr s9, [x0]\n"
        "    adrp x0, oOnReach\n"
        "    add x0, x0, :lo12:oOnReach\n"
        "    ldr x0, [x0]\n"
        "    blr x0\n"
        );

std::string (*oAppPlatForm_getFirstMachineCode)(void*);
std::string  (*oAppPlatForm_getMachineCode)(void*);
std::string (*oAppPlatForm_getMacAddr)(void*);
bool (*oAppPlatForm_isRoot)(void*);

void * (*oHttpRequest_HttpRequest)(void*,std::string&);
void * (*oNotifyManager_notifyList)(void*,std::string&,std::vector<std::string>&);

std::string (*oTestPatch)(std::string&) = NULL;

void* (*oSendMessageToJS)(void*) = NULL;
void* (*oSetScriptRuntimeInfo)(void*) = NULL;
Image* (*oSkin_getSkinImage)(void *a1, void *a2) = NULL;
Image* (*ovalidateAndResizeSkinData)(void *a1, void *a2,bool a3) = NULL;

std::string (*oFastWriter_write)(void*,void*) = NULL;
void* (*oChangeSkinWithSkinInfo)(void*,void*) = NULL;

void safeWrite(uintptr_t address, int value) {
    size_t pageSize = getpagesize();

    uintptr_t pageStart = address & ~(pageSize - 1);

    if (mprotect(reinterpret_cast<void*>(pageStart), pageSize, PROT_READ | PROT_WRITE | PROT_EXEC) == -1) {
        std::cerr << "Failed to change memory permissions!" << std::endl;
        return;
    }

    *reinterpret_cast<int*>(address) = value;

    if (mprotect(reinterpret_cast<void*>(pageStart), pageSize, PROT_READ | PROT_EXEC) == -1) {
        std::cerr << "Failed to restore memory permissions!" << std::endl;
    }
}


void Hooks::Init() {

    Arm64InlineHook* hooker = Arm64InlineHook::getInstance();

    // 以下4个Hook已移至S2CPacketCloudVar::handle()中主动执行
    // Player_normalTick, NetworkSystem_sendInternal, NetworkConnection_receivePacket, LevelRenderer_renderLevel

//    hooker->hookFunction(reinterpret_cast<void*>(GameData::base + 0x96482CC),
//                                     (void*)(&Hooks::Player_normalTick),
//                                     (void**)&oPlayer_normalTick);
//
//    hooker->hookFunction(reinterpret_cast<void*>(GameData::base + 0x7B39690),
//                                     (void*)(&Hooks::NetworkSystem_sendInternal),
//                                     (void**)&oNetworkSystem_sendInternal);
//
//    hooker->hookFunction(reinterpret_cast<void*>(GameData::base + 0x7B2F8A4),
//                                     (void*)(&Hooks::NetworkConnection_receivePacket),
//                                     (void**)&oNetworkConnection_receivePacket);
//
//    hooker->hookFunction(reinterpret_cast<void*>(GameData::base + 0x69213C8),
//                                     (void*)(&Hooks::LevelRenderer_renderLevel),
//                                     (void**)&oLevelRenderer_renderLevel);


    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x5389C60), (void*)(&Hooks::ClientInstance_tick),
                         (void**)&oClientInstance_tick);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x7B314EC), (void*)(&Hooks::flagReceiveSide),
                         (void**)&oFlagReceiveSide);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x6793850), (void*)(&Hooks::ScreenView_setupAndRender),
                         (void**)&oScreenView_setupAndRender);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x5EF1540), (void*)(&Hooks::HandleNoSlow),
                         (void**)&oHandleNoSlow);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x4EB6708), (void*)(&Hooks::OnInput),
                         (void**)&oOnInput);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x951EE74), (void*)(&Hooks::GameMode_getDestroyRate),
                         (void**)&oGameMode_getDestroyRate);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x68B3508), (void*)(&Hooks::LevelRendererPlayer_bobHurt),
                         (void**)&oLevelRendererPlayer_bobHurt);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x69482C8), (void*)(&Hooks::ItemRenderer_render),
                         (void**)&oItemRenderer_render);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x53EE7F4), (void*)(&Hooks::ItemInHandRenderer_renderItem),
                         (void**)&oItemInHandRenderer_renderItem);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xEF09580), (void*)(&Hooks::ParseJson),
                         (void**)&oParseJson);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xCD29534), (void*)(&Hooks::CRCCheck),
                         (void**)&oCRCCheck);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x5E80ED4), (void*)(&Hooks::doNoWeb),
                         (void**)&oDoNoWeb);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x5E30EF8), (void*)(&Hooks::doGroundAndAirMoveSystem),
                         (void**)&odoGroundAndAirMoveSystem);

//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x4C2A794), (void*)(&handleReach),
//                         (void**)&oOnReach);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x9521DCC), (void*)(&Hooks::GameMode_getPickRange),
                         (void**)&oGameMode_getPickRange);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x68B9C1C), (void*)(&Hooks::setupCamera),
                         (void**)&oSetupCamera);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xC418A3C), (void*)(&Hooks::getGameType),
                         (void**)&oGetGameType);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x51E12A4), (void*)(&Hooks::shouldDisplayPlayerPosition),
                         (void**)&oShouldDisplayPlayerPosition);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x51E8C80), (void*)(&Hooks::addChatMessage),
                         (void**)&oAddChatMessage);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xC437B7C), (void*)(&Hooks::playWorld),
                         (void**)&oPlayWorld);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xC630488), (void*)(&Hooks::decryptHttpResponse),
                         (void**)&oEasyUtil_decryptHttpContent);

//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x6B93AD4), (void*)(&Hooks::BlockTessellator_tessellateInWorld),
//                         (void**)&oBlockTessellator_tessellateInWorld);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x6BCA944), (void*)(&Hooks::BlockTessellator_tessellateSimpleBlockInWorld),
//                         (void**)&oBlockTessellator_tessellateSimpleBlockInWorld);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x5F5598C), (void*)(&Hooks::BlockActorRenderDispatcher_render),
//                         (void**)&oBlockActorRenderDispatcher_render);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x5F83BF0), (void*)(&Hooks::RenderChunkBuilder_sortBlocks),
//                         (void**)&oRenderChunkBuilder_sortBlocks);

    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x601F2F4), (void*)(&Hooks::Options_getGamma),
                         (void**)&oOptions_getGamma);

//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x48D37BC), (void*)(&Hooks::AppPlatForm_getFirstMachineCode),
//                         (void**)&oAppPlatForm_getFirstMachineCode);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x4798FE4), (void*)(&Hooks::AppPlatForm_getMachineCode),
//                         (void**)&oAppPlatForm_getMachineCode);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x4798D5C), (void*)(&Hooks::AppPlatForm_getMacAddr),
//                         (void**)&oAppPlatForm_getMacAddr);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x47987E8), (void*)(&Hooks::AppPlatForm_isRoot),
//                         (void**)&oAppPlatForm_isRoot);
//
    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xC6E52E4), (void*)(&Hooks::HttpRequest_HttpRequest),
                         (void**)&oHttpRequest_HttpRequest);

//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xA568FB0), (void*)(&Hooks::testPatch),
//                         (void**)&oTestPatch);


//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x5CCC00C), (void*)(&Hooks::Skin_getSkinImage),
//                         (void**)&oSkin_getSkinImage);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x7A21864), (void*)(&Hooks::validateAndResizeSkinData),
//                         (void**)&ovalidateAndResizeSkinData);



//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xD045CF4), (void*)(&Hooks::FastWriter_write),
//                         (void**)&oFastWriter_write);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xAFBA730), (void*)(&Hooks::ChangeSkinWithSkinInfo),
//                         (void**)&oChangeSkinWithSkinInfo);





//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0xA4167E4), (void*)(&Hooks::LoginStateMachine_change),
//                         (void**)&oLoginStateMachine_change);

//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x48D0AC8), (void*)(&Hooks::sendMessageToJS),
//                         (void**)&oSendMessageToJS);
//
//    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+0x479E21C), (void*)(&Hooks::setScriptRuntimeInfo),
//                         (void**)&oSetScriptRuntimeInfo);
}

#include "../Verify/Guard/FrameCounter.h"
#include "../Verify/Guard/RenderConfig.h"
#include "../Verify/Guard/EnvironmentScanner.h"
#include "../Verify/Guard/InlineVerify.h"

using namespace team::cool::client::guard;



void* Hooks::Player_normalTick(LocalPlayer* _this) {

    team::cool::client::guard::FrameCounter::tickFrame();

    static int tickLogCounter = 0;
    if (++tickLogCounter % 600 == 0) {
        bool s = team::cool::client::guard::FrameCounter::shouldContinueRendering();
        bool c = team::cool::client::guard::RenderConfig::validateRenderingConfig();
        bool e = team::cool::client::guard::EnvironmentScanner::isEnvironmentClean();
        LOGD("[VERIFY] Tick#%d: session=%s, config=%s, env=%s", 
             tickLogCounter, s?"OK":"FAIL", c?"OK":"FAIL", e?"OK":"FAIL");
             
        if(!s || !c || !e) {
             LOGW("[VERIFY] CHECK FAIL DETECTED");
        }
    }

    // Gate execution based on inline verification (no function call to patch)
    bool __inline_pass = true;
    INLINE_SECURITY_CHECK_V1(__inline_pass = false);
    if(!__inline_pass || !GameData::hasInit || _this!=GameData::clientInstance->getLocalPlayer()) {
        return oPlayer_normalTick(_this);
    }
    
    if(GameData::localplayer != _this) GameData::localplayer = _this;

    getEnv()->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onLocalPlayerTickMethod,reinterpret_cast<jlong>(_this));
    return oPlayer_normalTick(_this);

}



void* Hooks::ClientInstance_tick(ClientInstance *_this,void* a2) {
    if(GameData::clientInstance == nullptr) GameData::clientInstance = _this;
    getEnv()->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onClientTickMethod);
    return oClientInstance_tick(_this,a2);
}



void *Hooks::NetworkSystem_sendInternal(void* a1, void* a2, Packet* packet, std::string* data) {

    if(!GameData::hasInit){
        GameData::hasInit = true;
    }

    // STRICT GATING: Inline verification (V2 variant)
    bool __send_pass = true;
    INLINE_SECURITY_CHECK_V2(__send_pass = false);
    if (!__send_pass) {
        // If verification fails, behave as vanilla (pass through) or block custom logic
        return oNetworkSystem_sendInternal(a1, a2, packet, data);
    }

    //CustomPacket
    if(packet->getId() == 0){
        if(!reinterpret_cast<CustomWritePacket*>(packet)->callEvent){
            return oNetworkSystem_sendInternal(a1,a2,packet,data);
        }
    }

    JNIEnv* env = getEnv();
    jbyteArray arr = convertStringToByteArray(env,data);
    if(arr!= nullptr){
        jbyteArray byteArr = static_cast<jbyteArray>(env->CallStaticObjectMethod(
                JavaData::hookProxyClass, JavaData::onPacketSendMethod,
                arr));
        convertByteArrayToString(env,byteArr,data);
        env->DeleteLocalRef(arr);
        env->DeleteLocalRef(byteArr);
    }
    return oNetworkSystem_sendInternal(a1,a2,packet,data);
}


bool Hooks::NetworkConnection_receivePacket(void *_this, std::string* data, void *time1, void *time2,int flag) {

    if(!GameData::hasInit){
        GameData::hasInit = true;
    }

    if(flag==0x12345) return oNetworkConnection_receivePacket(_this,data,time1,time2,0);

    std::lock_guard<std::mutex> lock(GameData::packetMutex);
    if(!GameData::packetList.empty()){
        CustomReadPacket* firstElement = GameData::packetList.front();
        *data = *firstElement->data;
        GameData::packetList.erase(GameData::packetList.begin());
        if(!firstElement->callEvent){
            delete firstElement;
            return false;
        }
        delete firstElement;
    }else{
        bool ret = oNetworkConnection_receivePacket(_this,data,time1,time2,0);
        if(ret){
           return true;
        }
    }


    JNIEnv* env = getEnv();
    jbyteArray arr = convertStringToByteArray(env,data);
    if(arr!= nullptr){
        jbyteArray byteArr = static_cast<jbyteArray>(env->CallStaticObjectMethod(
                JavaData::hookProxyClass, JavaData::onPacketReceiveMethod,
                arr));
        if(byteArr != nullptr){
            jsize length = env->GetArrayLength(byteArr);
            if(length == 0){
                return true;
            }else{
                convertByteArrayToString(env,byteArr,data);
            }
        }
        env->DeleteLocalRef(arr);
        env->DeleteLocalRef(byteArr);
    }
    return false;
}

void *Hooks::flagReceiveSide(void* a1,void* a2,void* a3,void* a4) {
    return oFlagReceiveSide(a1,a2,a3,a4,0x12345);
}


void *Hooks::ScreenView_setupAndRender(ScreenView *_this, UIRenderContext *ctx) {
    if(!GameData::hasInit) return oScreenView_setupAndRender(_this,ctx);

    GameData::currentScreenName = _this->visualTree->mRootControl->name;
    getEnv()->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onPreRender2DMethod,reinterpret_cast<jlong>(_this),reinterpret_cast<jlong>(ctx));
    void* ret = oScreenView_setupAndRender(_this,ctx);
    getEnv()->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onPostRender2DMethod,reinterpret_cast<jlong>(_this),reinterpret_cast<jlong>(ctx));
    return ret;
}

void *Hooks::LevelRenderer_renderLevel(LevelRenderer *_this, ScreenContext *ctx,
                                       FrameRenderObject *frameRenderObject) {
    if(!GameData::hasInit) return oLevelRenderer_renderLevel(_this,ctx,frameRenderObject);

    if(GameData::test){
        register uintptr_t fp asm("x29");
        register uintptr_t lr asm("x30");

        uintptr_t original_fp = fp;
        uintptr_t original_lr = lr;

        fp = (uintptr_t)((char*)fp + 0x20);
        lr = (uintptr_t)0xdeadbeef;

        using fxxk = void* (__fastcall*)();
        reinterpret_cast<fxxk>(GameData::base+0x2834ECC)();
    }

    JNIEnv * env = getEnv();
    env->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onPreRender3DMethod,reinterpret_cast<jlong>(_this),reinterpret_cast<jlong>(ctx));
    void* ret = oLevelRenderer_renderLevel(_this,ctx,frameRenderObject);
    env->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onPostRender3DMethod);
    return ret;
}

void *Hooks::HandleNoSlow(void *a1, void *a2, void *a3, void *a4) {
    if(!GameData::doNoSlow){
        return oHandleNoSlow(a1,a2,a3,a4);
    }
    return a1;
}

void *Hooks::OnInput(void *_this, AInputEvent *event) {
    // Inline verification (V3 variant)
    bool __input_pass = true;
    INLINE_SECURITY_CHECK_V3(__input_pass = false);
    if(!__input_pass) return oOnInput(_this,event);
    if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_KEY) {
        int32_t keyCode = AKeyEvent_getKeyCode(event);
        int32_t action = AKeyEvent_getAction(event);
        JNIEnv * env = getEnv();
        env->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onKeyEventMethod,keyCode,action);
        if(action == AKEY_EVENT_ACTION_DOWN){
            GameData::keyMap[keyCode] = true;
        }else{
            GameData::keyMap[keyCode] = false;
        }

    }else if(AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION){

        int accessCount = 0;
        int action = AMotionEvent_getAction(event);
        int pointerCount = AMotionEvent_getPointerCount(event);
        for (int i = 0; i < pointerCount; i++) {
            float x = AMotionEvent_getX(event, i);
            float y = AMotionEvent_getY(event, i);
            JNIEnv * env = getEnv();
            if(!env->CallStaticBooleanMethod(JavaData::hookProxyClass,JavaData::onPressMethod,action,x,y,i)) accessCount++;
        }

        if(accessCount == pointerCount){
            return nullptr;
        }
    }
    return oOnInput(_this,event);
}

void *Hooks::OnResetMoveInput(void *a1, void *a2) {

    void* ret = oOnResetMoveInput(a1,a2);
    if(!GameData::hasInit) return ret;

    LocalPlayer* player = GameData::clientInstance->getLocalPlayer();
    if(GameData::doInvMove && player!= nullptr && GameData::currentScreenName.find("chat") == std::string::npos && GameData::currentScreenName!="hud_screen"){

        MoveInputComponent* component = player->getMoveInputHandler();

        bool w = GameData::keyMap[AKEYCODE_W] || component->isUpDown;
        bool a = GameData::keyMap[AKEYCODE_A] || component->isLeftDown;
        bool s = GameData::keyMap[AKEYCODE_S] || component->isDownDown;
        bool d = GameData::keyMap[AKEYCODE_D] || component->isRightDown;
        bool sneak = component->isSneakDown;
        float moveforward = 0;
        float moveSide = 0;
        if(w){
            if(sneak) moveforward+=0.3f;
            else moveforward+=1;
        }

        if(s){
            if(sneak) moveforward-=0.3f;
            else moveforward-=1;
        }

        if(a){
            if(sneak) moveSide+=0.3f;
            else moveSide+=1;
        }

        if(d){
            if(sneak) moveSide-=0.3f;
            else moveSide-=1;
        }

        if(a && w || a && s || d && w || d && s){
            moveforward*=0.707f;
            moveSide*=0.707f;
        }

        component->moveForward = moveforward;
        component->moveSide = moveSide;
        component->isSprintDown = true;
    }
    return ret;
}



void *Hooks::OnHandleJumpInput(void *a1, void *a2, void *a3, char* a4,void* a5,void* a6) {
    if(GameData::hasInit && GameData::doInvMove && GameData::currentScreenName.find("chat") == std::string::npos && GameData::currentScreenName!="hud_screen") {
        a4[0x90] = GameData::keyMap[AKEYCODE_SPACE] || a4[0x90];
    }
    return oOnHandleJumpInput(a1,a2,a3,a4,a5,a6);
}



float Hooks::GameMode_getDestroyRate(GameMode *_this, Block *block) {
    float old = oGameMode_getDestroyRate(_this,block);
    if(!GameData::hasInit) return old;
    JNIEnv * env = getEnv();
    Vec3i blockPos = _this->destroyBlockPos;
    return env->CallStaticFloatMethod(JavaData::hookProxyClass,JavaData::onDestroyBlockMethod,reinterpret_cast<jlong>(_this->player),reinterpret_cast<jlong>(block),blockPos.x,blockPos.y,blockPos.z,old);
}

void* Hooks::LevelRendererPlayer_bobHurt(LevelRenderer *_this, Matrix *matrix, float idk) {
    JNIEnv * env = getEnv();
    float* arrayAddr = matrix->m;
    jfloatArray floatArray = env->NewFloatArray(16);
    if(floatArray == NULL){
        return oLevelRendererPlayer_bobHurt(_this,matrix,idk);
    }
    env->SetFloatArrayRegion(floatArray,0,16,arrayAddr);
    jfloatArray result = static_cast<jfloatArray>(env->CallStaticObjectMethod(
            JavaData::hookProxyClass, JavaData::onHurtBobMethod, floatArray));
    env->GetFloatArrayRegion(result, 0, 16, arrayAddr);
    env->DeleteLocalRef(floatArray);
    env->DeleteLocalRef(result);
    return oLevelRendererPlayer_bobHurt(_this,matrix,idk);
}


void *Hooks::setupCamera(LevelRendererPlayer *thiz, void *camera) {
    void* ret = oSetupCamera(thiz,camera);
    // if(!checkVerification()) return ret;
    JNIEnv * env = getEnv();
    Vec3& pos = thiz->targetCameraPos;
    jfloatArray result = static_cast<jfloatArray>(env->CallStaticObjectMethod(JavaData::hookProxyClass,JavaData::onSetCameraMethod,pos.x,pos.y,pos.z));
    env->GetFloatArrayRegion(result, 0, 3, reinterpret_cast<jfloat *>(&pos));
    env->DeleteLocalRef(result);
    return ret;
}


void *Hooks::ItemRenderer_render(u_int64_t _this, u_int64_t a1, u_int64_t* a2) {
    if(GameData::doItemNoRot){
        *(char *)(*a2 + 1970) = 1;
    }
    return oItemRenderer_render(_this,a1,a2);
}

void *Hooks::ItemInHandRenderer_renderItem(void *_this, BaseActorRenderContext *ctx, Actor *actor,ItemStack* item,bool posAndRotSetByJSON,int flag,bool renderContext,int idk) {

    if(flag != 2){
        return oItemInHandRenderer_renderItem(_this,ctx,actor,item, posAndRotSetByJSON,flag,renderContext,idk);
    }

    JNIEnv * env = getEnv();
    float* arrayAddr = ctx->screenContext->camera->modelMatrixStack.stack.top().m;
    float temp[16];
    for (int i = 0; i < 16; ++i) {
        temp[i] = arrayAddr[i];
    }

    jfloatArray floatArray = env->NewFloatArray(16);
    if(floatArray == NULL){
        return oItemInHandRenderer_renderItem(_this,ctx,actor,item,posAndRotSetByJSON,flag,renderContext,idk);
    }

    env->SetFloatArrayRegion(floatArray,0,16,arrayAddr);
    jfloatArray result = static_cast<jfloatArray>(env->CallStaticObjectMethod(
            JavaData::hookProxyClass, JavaData::onRenderItem3DMethod, ctx->screenContext->partialTicks, reinterpret_cast<jlong>(actor), floatArray));
    env->GetFloatArrayRegion(result, 0, 16, arrayAddr);
    env->DeleteLocalRef(floatArray);
    env->DeleteLocalRef(result);
    void* ret = oItemInHandRenderer_renderItem(_this,ctx,actor,item,posAndRotSetByJSON,flag,renderContext,idk);
    float* backAddr = ctx->screenContext->camera->modelMatrixStack.stack.top().m;
    for (int i = 0; i < 16; ++i) {
        backAddr[i] = temp[i];
    }
    return ret;
}

void *Hooks::ParseJson(void *a1, std::string *a2, void *a3, void *a4) {

    if(a2 != nullptr && !GameData::cookie.empty()){
        if (a2->find(R"("gameid":"x19")") != std::string::npos) {
            *a2 = GameData::cookie;
        }
    }

    return oParseJson(a1,a2,a3,a4);
}


void* Hooks::CRCCheck(void *a1, void *a2, void *a3) {
    return nullptr;
}

void *Hooks::doNoWeb(void *a1, void *a2, void *a3,void* a4) {
    if(GameData::doNoWeb){
        return nullptr;
    }else{
        return oDoNoWeb(a1,a2,a3,a4);
    }
}

void *Hooks::doGroundAndAirMoveSystem(StrictEntityContext *a1, void *a2, void *a3, void *a4, void *a5, void *a6,
                                      void *a7, void *a8, void *a9, void *a10, void *a11, void *a12,
                                      void *a13) {

    void* ret = odoGroundAndAirMoveSystem(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12,a13);
    // if(!checkVerification()) return ret;
    LocalPlayer* player = GameData::clientInstance->getLocalPlayer();
    if(player!= nullptr && player->mEntityContext.mEntity == a1->entityID){
        getEnv()->CallStaticVoidMethod(JavaData::hookProxyClass,JavaData::onPostMoveMethod,reinterpret_cast<jlong>(player));
    }
    return ret;
}

float Hooks::GameMode_getPickRange(GameMode *thiz, InputMode *inputMode, bool isVR) {
    if(GameData::buildReach <= 0){
        return oGameMode_getPickRange(thiz,inputMode,isVR);
    }
    return GameData::buildReach;
}

int Hooks::getGameType(void *type) {
    return 100;
}

bool Hooks::shouldDisplayPlayerPosition(void *thiz) {
    if(GameData::forcePos){
        return true;
    }
    return oShouldDisplayPlayerPosition(thiz);
}

std::string toHexLog2(const std::string* str) {
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


void *Hooks::addChatMessage(void *thiz, std::string &message) {
    JNIEnv * env = getEnv();
    jboolean result = env->CallStaticBooleanMethod(JavaData::hookProxyClass,JavaData::onChatMethod,env->NewStringUTF(message.c_str()));
    if(result) return nullptr;
    return oAddChatMessage(thiz,message);
}

void *
Hooks::playWorld(void *a1, void *a2, void *a3, std::optional<RoomInfo>& roomInfo, void *a5, std::optional<SkinInfo>& skinInfo,
                 void *a7, void *a8, void *a9) {

    if(!GameData::nextIP.empty()){
        roomInfo->ip = GameData::nextIP;
        roomInfo->port = GameData::nextPort;
    }

    if(GameData::skinType == 1){
        skinInfo->skinPath = "custom.zip";
        skinInfo->skinIID = "114514";
//        skinInfo->inPackage = false;
//        skinInfo->skinSync = false;
//        skinInfo->skinSlim = false;
    }

    if(GameData::skinType == 2){
        skinInfo->skinPath = "custom.png";
        skinInfo->skinIID = "114514";
//        skinInfo->inPackage = false;
//        skinInfo->skinSync = false;
//        skinInfo->skinSlim = false;
    }

    GameData::lastIP = roomInfo->ip + ":" + std::to_string(roomInfo->port);
    return oPlayWorld(a1,a2,a3,roomInfo,a5,skinInfo,a7,a8,a9);
}

std::string Hooks::decryptHttpResponse(const std::string& response,int factor){

    std::string patch_str = "autopatch";
    std::string replaced = "nil";
    std::string ret = oEasyUtil_decryptHttpContent(response,factor);
    size_t pos = 0;

    while ((pos = ret.find(patch_str, pos)) != std::string::npos) {
        ret.replace(pos, patch_str.length(), replaced);
        pos += replaced.length();
    }

    return ret;
}

void *Hooks::BlockTessellator_tessellateInWorld(void *thiz, void *a2, Block *a3, void *a4, void *a5,
                                                void *a6) {

    if(!GameData::doXRay) return oBlockTessellator_tessellateInWorld(thiz,a2,a3,a4,a5,a6);

    if(std::find(GameData::xRayList.begin(), GameData::xRayList.end(), a3->legacy->getNameSpace()) != GameData::xRayList.end()){
        return oBlockTessellator_tessellateInWorld(thiz,a2,a3,a4,a5,a6);
    }

    return nullptr;
}

void* Hooks::BlockTessellator_tessellateSimpleBlockInWorld(void* thiz,void* a2,Block* a3,void* a4,void* a5){
    if(!GameData::doXRay) return oBlockTessellator_tessellateSimpleBlockInWorld(thiz,a2,a3,a4,a5);

    if(std::find(GameData::xRayList.begin(), GameData::xRayList.end(), a3->legacy->getNameSpace()) != GameData::xRayList.end()){
        return oBlockTessellator_tessellateSimpleBlockInWorld(thiz,a2,a3,a4,a5);
    }

    return nullptr;
}


void *Hooks::BlockActorRenderDispatcher_render(void *thiz, void *a2, void *a3, BlockActor *a4, Block *a5,
                                         void *a6, void *a7, void *a8, void *a9, void *a10,
                                         void *a11, void *a12) {

    if(!GameData::doXRay) return oBlockActorRenderDispatcher_render(thiz,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12);

    if(a4->blockPos.x == INT_MIN){
        return oBlockActorRenderDispatcher_render(thiz,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12);
    }

    if(GameData::doChestXray && a5->legacy->getNameSpace() == "minecraft:chest") return oBlockActorRenderDispatcher_render(thiz,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12);
    return nullptr;
}



void* Hooks::RenderChunkBuilder_sortBlocks(RenderChunkBuilder* thiz,void* region,void* renderChunkGeometry,void* needAirAndSimpleBlocks,AirAndSimpleBlockBits* airAndSimpleBlocks) {

    void* ret = oRenderChunkBuilder_sortBlocks(thiz,region,renderChunkGeometry,needAirAndSimpleBlocks,airAndSimpleBlocks);
    if(!GameData::doXRay) return ret;

    auto& queue = *(reinterpret_cast<RenderChunkBuilder*>(thiz)->mQueues);
    for (auto it = queue.begin(); it != queue.end(); ) {
        if (std::find(GameData::xRayList.begin(), GameData::xRayList.end(), it->blockInfo->legacy->getNameSpace()) == GameData::xRayList.end()) {
            it = queue.erase(it);
        } else {
            ++it;
        }
    }

    airAndSimpleBlocks->mAirBlocks.set();
    airAndSimpleBlocks->mSimpleBlocks.reset();
    return ret;
}


float Hooks::Options_getGamma(void* thiz) {
    if(GameData::gamma<=0) return oOptions_getGamma(thiz);
    return GameData::gamma;
}

void Hooks::doVerify(uintptr_t off) {
    Arm64InlineHook* hooker = Arm64InlineHook::getInstance();
    hooker->hookFunction(reinterpret_cast<void *>(GameData::base+off), (void*)(&Hooks::OnInput),
                         (void**)&oOnInput);
}


std::string Hooks::AppPlatForm_getFirstMachineCode(void *thiz) {
    return GameData::loginUdid;
}

std::string Hooks::AppPlatForm_getMachineCode(void *thiz) {
    return GameData::loginUdid;
}

std::string Hooks::AppPlatForm_getMacAddr(void *thiz) {
    return GameData::macAddr;
}

bool Hooks::AppPlatForm_isRoot(void *thiz) {
    return false;
}

void *Hooks::HttpRequest_HttpRequest(void* thiz,std::string & content) {
    if(content.find("salog") != std::string::npos){
        content = "None/authentication/reconnect";
        return oHttpRequest_HttpRequest(thiz,content);
    }

//    if(content.find("https://g79.update.netease.com/serverlist/adr_release.0.17.json") != std::string::npos){
//        content = "https://g79.update.netease.com/serverlist/gaiya.json";
//        return oHttpRequest_HttpRequest(thiz,content);
//    }

    return oHttpRequest_HttpRequest(thiz,content);
}


void *Hooks::NotifyManager_notifyList(void *thiz, std::string& eventName, std::vector<std::string> &data) {
    return oNotifyManager_notifyList(thiz,eventName,data);
}

std::string test(JNIEnv *env, jstring jStr) {

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

std::string Hooks::testPatch(std::string &content) {
    JNIEnv * env = getEnv();
    jstring str = static_cast<jstring>(getEnv()->CallStaticObjectMethod(JavaData::hookProxyClass,
                                                                        JavaData::onEncryptMethod,
                                                                        env->NewStringUTF(
                                                                                content.c_str())));
    content = test(env,str);
    return oTestPatch(content);
}

void *Hooks::sendMessageToJS(void *) {
    return nullptr;
}

void *Hooks::setScriptRuntimeInfo(void *) {
    return nullptr;
}

void *Hooks::LoginStateMachine_change(void *thiz, int stage) {
//    if(stage >= 0x7 ) GameData::initSuccess = true;
//    return oLoginStateMachine_change(thiz,stage);
//    if(stage >= 15){
//        using fxxk = void* (__fastcall*)();
//        reinterpret_cast<fxxk>(GameData::base+0x2834ECC)();
//    }

    return oLoginStateMachine_change(thiz,stage);
}


Image *Hooks::Skin_getSkinImage(void *a1,void* a2) {
    Image* ret = oSkin_getSkinImage(a1,a2);
    if(!GameData::doCrash) return ret;

    auto caller = (uintptr_t)__builtin_return_address(0);

    if(caller-GameData::base == 0x6293FAC){
        ret->width = 4096;
        ret->height = 4096;
    }

    return ret;
}

Image *Hooks::validateAndResizeSkinData(void *thiz, Image *image, bool isPremium) {
    if(GameData::doCrash) {
        if(image->width == 4096 && image->height == 4096){
            image->width = 64;
            image->height = 64;
        }
    }
    return ovalidateAndResizeSkinData(thiz,image,isPremium);
}


std::string Hooks::FastWriter_write(void *thiz, void *value) {
    std::string ret = oFastWriter_write(thiz,value);
    // 不是永久用户返回正常
    if(GameData::verifyTime != -1) return ret;
    if(ret.find("AnimatedImageData") != std::string::npos){
        JNIEnv * env = getEnv();
        jstring str = static_cast<jstring>(getEnv()->CallStaticObjectMethod(JavaData::hookProxyClass,
                                                                            JavaData::onLoginMethod,
                                                                            env->NewStringUTF(
                                                                                    ret.c_str())));
        ret = test(env,str);
    }
    return ret;
}


void *Hooks::ChangeSkinWithSkinInfo(void *a1, void *a2) {
    // 不是永久用户返回正常
    if(GameData::verifyTime != -1) return oChangeSkinWithSkinInfo(a1,a2);
    if(GameData::skinType != 0) return nullptr;
    return oChangeSkinWithSkinInfo(a1,a2);
}