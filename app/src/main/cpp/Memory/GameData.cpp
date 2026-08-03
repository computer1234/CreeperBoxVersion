#include "GameData.h"
#include <vector>

uintptr_t GameData::base = 0;
uintptr_t GameData::end = 0;
LocalPlayer* GameData::localplayer = nullptr;
ClientInstance* GameData::clientInstance = nullptr;
std::vector<CustomReadPacket*> GameData::packetList;
std::mutex GameData::packetMutex;
bool GameData::doNoSlow = false;
bool GameData::doInvMove = false;
bool GameData::keyMap[400];
std::string GameData::currentScreenName = std::string();
bool GameData::hasFailure = false;
float GameData::verify = 9999999;
int GameData::setVerify = 1;
bool GameData::hasInit = false;
bool GameData::doItemNoRot = false;
std::string GameData::cookie;
std::string GameData::deviceID;
bool GameData::doNoWeb = false;
float GameData::reach = 3;
float GameData::buildReach = 10;
bool GameData::forcePos = false;
std::string GameData::lastIP;
std::string GameData::nextIP;
unsigned short GameData::nextPort = 0;
std::vector<std::string> GameData::xRayList;
bool GameData::doXRay;
bool GameData::doChestXray;
float GameData::gamma = 0;
bool GameData::test = false;
int64_t GameData::randomIV = 0;
std::string GameData::loginUdid;
std::string GameData::macAddr;
bool GameData::initSuccess = false;
long long GameData::verifyTime = 99999999999999999L;
bool GameData::doCrash = false;
int GameData::skinType = 0;

void GameData::initializeMemoryConfig(int64_t seed) {
    // Stub implementation to satisfy linker
    // You can implement actual logic here if needed, e.g. setting randomIV or other config
    randomIV = seed; 
}