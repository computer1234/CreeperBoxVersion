#include "Level.h"
#include "../Utils/MemUtils.h"
#include "util/Log.h"
#include <vector>


Tick *Level::getCurrentTick() {
    return CallVFunc<77, Tick *>(this);
}

BlockPalette *Level::getBlockPalette() {
    return CallVFunc<83, BlockPalette*>(this);
}

void Level::forEachPlayer(std::function<bool(Player &)> function) {
    CallVFunc<240, void,std::function<bool(Player &)>>(this,function);      //GameRuleCommand
}

bool Level::isClientSide() {
    return CallVFunc<410, bool>(this);
}

std::unordered_map<UUID, PlayerListEntry> &Level::getPlayerList() {
    return CallVFunc<411, std::unordered_map<UUID, PlayerListEntry> &>(this);;
}


std::vector<Actor*> Level::getRuntimeActorList() {
    return CallVFunc<417, std::vector<Actor*>>(this);
}


LoopbackPacketSender *Level::getPacketSender() {
    return CallVFunc<420, LoopbackPacketSender *>(this);
}

HitResult *Level::getHitResult() {
    return CallVFunc<428, HitResult *>(this);
}








