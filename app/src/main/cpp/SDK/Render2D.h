#ifndef HOPECLIENT_RENDER2D_H
#define HOPECLIENT_RENDER2D_H

#include <cstdint>
#include <string>
#include "Math.h"
#include <vector>
#include <set>


class ScreenContext;
class ClientInstance;
class MinecraftGame;

class ItemRenderer;

class BaseActorRenderContext{
private:
    void* vTable;                                   //0x00
    float mNumEntitiesRenderedThisFrame;            //0x08
    float mLastFrameTime;                           //0x0C
    void* mSortedMeshDrawList;                      //0x10
    void* mClientInstance;                          //0x18
    void* mMinecraftGame;                           //0x20
public:
    ScreenContext* screenContext;                   //0x28
private:
    void* mBlockEntityRenderDispatcher;             //0x30
    char mEntityRenderDispatcher[16];               //0x38 (shared_ptr, 16 bytes)
    void* mEntityBlockRenderer;                     //0x48
    void* mItemInHandRenderer;                      //0x50
public:
    ItemRenderer* renderer;                         //0x58 (mItemRenderer)
private:
    char pad0060[0x400];
public:
    BaseActorRenderContext(ScreenContext* ctx);
};

class GuiData {
private:
    char pad0000[0x50];
public:
    float scale;    //0x50
    float invGuiScale;      //0x54
    bool isCurrentlyActive;        //0x58
    std::set<int> postedErrors;       //0x60
    bool pointerPressed;             //0x78
    __int16_t pointerX;          //0x7A
    __int16_t pointerY;         //0x7C
    __int16_t padding;
public:
    char pad0080[0x238];
public:
    bool showProgress;    //0x2B8
};



class ItemStack;
class ItemRenderer {
public:
    //idk 0.0
    void renderGuiItemNew(BaseActorRenderContext *BaseActorRenderCtx, ItemStack *item, int mode, float x, float y, float opacity, float scale,float idk, bool isEnchanted);
};


class ScreenController{
public:
    void* tryExit();
    void* _handlePlaceAll(std::string const &,int);
    void* _handlePlaceOne(std::string const &,int);
    ItemStack* _getItemStack(std::string const&,int);
};


class UIRenderContext{
public:
    void* vTable;
    ClientInstance* clientInstance;
    ScreenContext* screenContext;
};


class UIControl;

class UIComponent {
public:
    void* vTable;
    UIControl* owner;
    bool isScreenComponent();
};



class TextComponent : public UIComponent {

};



class ScreenSettings : public UIComponent {
public:
    void setAlwaysAcceptsInput(bool value);
    bool isAlwaysAcceptsInput();
    void setAbsorbsInput(bool value);
    bool isAbsorbsInput();
    void setShowingMenu(bool value);
    bool isShowingMenu();
    void setForceRenderBelow(bool value);
    bool isForceRenderBelow();
};



class UIControl {
public:
    char padding0000[0x60];
    std::string name;     //0x60
    Vec2 parentRelativePosition;       //0x78
    Vec2 size;    //0x80
    Vec2 maxSize;   //0x88
    Vec2 minSize;    //0x90
    float alpha;   //0x98
    int order;    //0x9B
    int layer;    //0xA0
    char padding00A4[0x24];
    std::shared_ptr<UIControl> parent;     //0xC8
    std::vector<std::shared_ptr<UIControl>> children;     //0xD8
    char padding00F0[0x8];
    std::vector<std::unique_ptr<UIComponent>> components;    //0xF8
public:
    void iterateAllControls(std::function<void(std::shared_ptr<UIControl> control)> func);
    void iterateChildControls(std::function<void(std::shared_ptr<UIControl> control)> func);
};



class VisualTree{
private:
    uintptr_t **vTable;
public:
    UIControl* mRootControl;   //0x08
};


class ScreenView {
public:
    char padding0000[0x38];
    ScreenController* currentController;    //0x38
    char padding0040[0x8];
    VisualTree* visualTree;  //0x48
};

#endif //HOPECLIENT_RENDER2D_H
