#include "Render2D.h"
#include "../Utils/MemUtils.h"
#include "../Memory/GameData.h"
#include <cstdint>

void UIControl::iterateAllControls(std::function<void(std::shared_ptr<UIControl>)> func) {
    std::shared_ptr<UIControl> self = std::shared_ptr<UIControl>(this, [](UIControl*){});
    func(self);

    for (size_t i = 0; i < children.size(); i++) {
        auto child = children[i];
        func(child);
        child->iterateAllControls(func);
    }
}

void UIControl::iterateChildControls(std::function<void(std::shared_ptr<UIControl>)> func) {
    for (size_t i = 0; i < children.size(); i++) {
        auto child = children[i];
        func(child);
    }
}

void* ScreenController::tryExit() {
    return CallVFunc<13,void*>(this);
}

void* ScreenController::_handlePlaceAll(const std::string & name, int slot) {
    return CallVFunc<58,void*,const std::string&,int>(this,name,slot);
}

void* ScreenController::_handlePlaceOne(const std::string & name, int slot) {
    return CallVFunc<59,void*,const std::string&,int>(this,name,slot);
}

ItemStack *ScreenController::_getItemStack(const std::string & name, int slot) {
    return CallVFunc<66,ItemStack*,const std::string&,int>(this,name,slot);
}

void ScreenSettings::setAlwaysAcceptsInput(bool value) {
    *(int64_t *)(this + 24) = *(int64_t *)(this + 24) & 0xFFFFFFFD | (2 * value);
}

bool ScreenSettings::isAlwaysAcceptsInput() {
    return (*(unsigned char *)(this + 24) >> 1) & 1;
}

void ScreenSettings::setAbsorbsInput(bool value) {
    *(int64_t *)(this + 24) = *(int64_t *)(this + 24) & 0xFFFFFFF7 | (8 * value);
}

bool ScreenSettings::isAbsorbsInput() {
    return (*(unsigned char *)(this + 24) >> 3) & 1;
}

void ScreenSettings::setShowingMenu(bool value) {
    *(int64_t *)(this + 24) = *(int64_t *)(this + 24) & 0xFFFFFFEF | (16 * value);
}

bool ScreenSettings::isShowingMenu() {
    return (*(unsigned char *)(this + 24) >> 4) & 1;
}

void ScreenSettings::setForceRenderBelow(bool value) {
    *(int64_t *)(this + 24) = *(int64_t *)(this + 24) & 0xFFFFFDFF | (value << 9);
}

bool ScreenSettings::isForceRenderBelow() {
    return (*(unsigned char *)(this + 25) >> 2) & 1;;
}


void ItemRenderer::renderGuiItemNew(BaseActorRenderContext *BaseActorRenderCtx, ItemStack *item,
                                    int mode, float x, float y, float opacity, float scale,
                                    float idk, bool isEnchanted) {

    //Fucking netease
    using Fn = void* (__fastcall*)(void*,void*,void*,int,bool,float,float,float,float,float);
    reinterpret_cast<Fn>(GameData::base+0x65E9768)(this,BaseActorRenderCtx,item,mode,isEnchanted,x,y,opacity,scale,idk);
}


BaseActorRenderContext::BaseActorRenderContext(ScreenContext *ctx) {
    using Fn = void* (__fastcall*)(void*,void*,void*,void*);
    ClientInstance* ci = GameData::clientInstance;
    reinterpret_cast<Fn>(GameData::base+0x5478208)(this,ctx,ci,ci->minecraftGame);
}
