#ifndef HOPECLIENT_GAMEDATA_H
#define HOPECLIENT_GAMEDATA_H
#include <cstdint>
#include "../SDK/Actor.h"
#include "../SDK/ClientInstance.h"


class GameData {
public:
    static int setVerify;
    static uintptr_t base;
    static uintptr_t end;
    static LocalPlayer* localplayer;
    static ClientInstance* clientInstance;
    static std::vector<CustomReadPacket*> packetList;
    static std::mutex packetMutex;
    static bool doNoSlow;
    static bool keyMap[400];
    static std::string currentScreenName;
    static bool doInvMove;
    static bool hasFailure;
    static float verify;         // > 1000 is verify
    static bool hasInit;
    static bool doItemNoRot;
    static std::string cookie;
    static std::string deviceID;
    static bool doNoWeb;
    static float reach;
    static float buildReach;
    static bool forcePos;
    static std::string lastIP;
    static std::string nextIP;
    static unsigned short nextPort;
    static std::vector<std::string> xRayList;
    static bool doXRay;
    static bool doChestXray;
    static float gamma;
    static bool test;
    static int64_t randomIV;
    static std::string loginUdid;
    static std::string macAddr;
    static bool initSuccess;
    static long long verifyTime;
    static bool doCrash;
    static int skinType;    //0 doNothing  1 zipFormat  2 pngFormat
    static void initializeMemoryConfig(int64_t seed);
};





#endif //HOPECLIENT_GAMEDATA_H
