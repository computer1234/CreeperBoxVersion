#include "ClientInstance.h"
#include "../Memory/GameData.h"

BlockSource* ClientInstance::getRegion() {
    return CallVFunc<34, BlockSource *>(this);
}

LocalPlayer* ClientInstance::getLocalPlayer() {
    return CallVFunc<35, LocalPlayer *>( this);
}

Options *ClientInstance::getOptions() {
    return CallVFunc<205, Options*>( this);
}

GuiData *ClientInstance::getGuiData() {
    return *reinterpret_cast<GuiData**>((uintptr_t)(this) + 0x6B8);    //ui_invert_overlay
}

void ClientInstance::handleDestroyOrAttackButtonPress() {
    using Fn = void(__thiscall*)(void*);
    reinterpret_cast<Fn>(GameData::base+0x6C51CA0)(this);
}

void ClientInstance::handleBuildOrInteractButtonPress() {
    using Fn = void(__thiscall*)(void*);
    reinterpret_cast<Fn>(GameData::base+0x50BDF1C)(this);
}

int Options::getPlayerViewPerspective() {
    return CallVFunc<139,int>(this);
}
