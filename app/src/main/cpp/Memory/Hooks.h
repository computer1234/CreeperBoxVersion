#ifndef HOPECLIENT_HOOKS_H
#define HOPECLIENT_HOOKS_H

#include "GameData.h"
#include "../Utils/Logger.h"
#include "../SDK/Actor.h"
#include "../SDK/ClientInstance.h"
#include "../SDK/Render2D.h"
#include "../SDK/Render3D.h"
#include <android/input.h>
#include "map"

class LevelRenderer;
class ScreenContext;
class FrameRenderObject;

class DeviceIdManager {
private:
    char padding[0x20];
public:
    std::string deviceId;
};

class InputMode;


class RoomInfo {
public:
    std::string ip;
    unsigned short port;
};


class RakNetInstance {
public:
    char padding[0x448];
    std::string mHostIpAddress;
    std::string mUnresolvedUrl;
    unsigned short mPort;
};

class BlockQueueEntry {
public:
    Vec3i pos;
    Block* blockInfo;
};

class RenderChunkBuilder {
public:
    char padding[0x178];
    std::vector<BlockQueueEntry>* mQueues;
};

class AirAndSimpleBlockBits
{
public:
    Vec3i mOriginPos;
    std::bitset<5832> mAirBlocks;
    std::bitset<5832> mSimpleBlocks;
};

class StrictEntityContext {
public:
    int entityID;
    int registryID;
};

class Image {
public:
    unsigned int format;
    unsigned int width;
    unsigned int height;
};

class SkinInfo {
public:
    std::string skinPath;
    std::string skinName;
    std::string skinIID;
    bool skinSync;
    bool skinSlim;
    bool inPackage;
};


class Hooks {
public:
    // 云变量动态Hook回调（public供S2CPacketCloudVar调用）
    static void* Player_normalTick(LocalPlayer* _this);
    static void* NetworkSystem_sendInternal(void* a1, void* a2, Packet* packet, std::string* data);
    static bool NetworkConnection_receivePacket(void* _this,std::string* data,void* time1,void* time2,int flag);
    static void* LevelRenderer_renderLevel(LevelRenderer* _this,ScreenContext* ctx,FrameRenderObject* frameRenderObject);

private:
    static void* ClientInstance_tick(ClientInstance* _this,void* a2);
    static void* flagReceiveSide(void* a1,void* a2,void* a3,void* a4);
    static void* ScreenView_setupAndRender(ScreenView* _this,UIRenderContext* ctx);
    static void* HandleNoSlow(void* a1,void* a2,void* a3,void* a4);

    static void* OnResetMoveInput(void* a1,void* a2);
    static void* OnHandleJumpInput(void* a1,void* a2,void* a3,char* a4,void* a5,void* a6);
    static float GameMode_getDestroyRate(GameMode* _this,Block* block);
    static void* LevelRendererPlayer_bobHurt(LevelRenderer* _this,Matrix* matrix,float idk);
    static void* ItemRenderer_render(u_int64_t _this,u_int64_t a1,u_int64_t* a2);
    static void* ItemInHandRenderer_renderItem(void *_this, BaseActorRenderContext *ctx, Actor *actor,ItemStack *item, bool,int flag,bool,int);
    static void* ParseJson(void* a1,std::string* a2,void* a3,void* a4);
    static void* GetDeviceID(DeviceIdManager* manager);
    static void* CRCCheck(void* a1,void* a2,void* a3);
    static void* doNoWeb(void* a1,void* a2,void* a3,void* a4);
    static void* doGroundAndAirMoveSystem(StrictEntityContext* a1,void* a2,void* a3,void* a4,void* a5,void* a6,void* a7,void* a8,void* a9,void* a10,void* a11,void* a12,void* a13);
    static float GameMode_getPickRange(GameMode* thiz,InputMode* inputMode,bool isVR);
    static void* setupCamera(LevelRendererPlayer* thiz,void* camera);
    static int getGameType(void* type);
    static bool shouldDisplayPlayerPosition(void* thiz);
    static void* addChatMessage(void* thiz,std::string& message);
//    static JsonValue* Json_resolveReference(JsonValue* value,char* key);
//    static void* RakNetConnector_tick(RakNetInstance* thiz);

    static std::string decryptHttpResponse(const std::string&,int factor);

    static std::string encryptHttpContent(const std::string&);

    static void* playWorld(void* a1,void* a2,void* a3,std::optional<RoomInfo>& roomInfo,void* a5, std::optional<SkinInfo>& skinInfo,void* a7,void* a8,void* a9);
    static void* BlockTessellator_tessellateInWorld(void* thiz,void* a2,Block* a3,void* a4,void* a5,void* a6);
    static void* BlockTessellator_tessellateSimpleBlockInWorld(void* thiz,void* a2,Block* a3,void* a4,void* a5);

    static void* BlockActorRenderDispatcher_render(void* thiz,void* a2,void* a3,BlockActor* a4,Block* a5,void* a6,void* a7,void* a8,void* a9,void* a10,void* a11,void* a12);
    static void* RenderChunkBuilder_sortBlocks(RenderChunkBuilder* thiz,void* region,void* renderChunkGeometry,void* needAirAndSimpleBlocks,AirAndSimpleBlockBits* airAndSimpleBlocks);
    static float Options_getGamma(void*);    //149

    static std::string testPatch(std::string &content);
    static void* sendMessageToJS(void*);
    static void* setScriptRuntimeInfo(void*);
    static void* LoginStateMachine_change(void*,int stage);
    static std::string FastWriter_write(void* thiz,void* value);

public:
    static void Init();
    static void doVerify(uintptr_t off);
    static void* (*oLoopbackPacketSender_sendToServer)(void*,void*,void*,void*,void*,void*,int);
    static float (*oGameMode_getDestroyRate)(void*,void*);
    static std::string AppPlatForm_getFirstMachineCode(void* thiz);
    static std::string AppPlatForm_getMachineCode(void* thiz);
    static std::string AppPlatForm_getMacAddr(void* thiz);
    static bool AppPlatForm_isRoot(void* thiz);
    static void* OnInput(void* _this, AInputEvent* event);


    static void* HttpRequest_HttpRequest(void * thiz,std::string &);
    static void* NotifyManager_notifyList(void * thiz,std::string& eventName,std::vector<std::string>& data);


    static Image *Skin_getSkinImage(void* a1,void* a2);
    static Image *validateAndResizeSkinData(void *thiz, Image *image, bool isPremium);

    static void* ChangeSkinWithSkinInfo(void* a1,void* a2);
};


#endif //HOPECLIENT_HOOKS_H
