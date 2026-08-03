#ifndef HOPECLIENT_CLIENTINSTANCE_H
#define HOPECLIENT_CLIENTINSTANCE_H


#include "Actor.h"
#include "BlockSource.h"
#include "Render2D.h"

class Timer{
public:
    float tps;   //0x0
    float ticks;      //0x4
    float alpha;       //0x8
    float timeScale;      //0xC
    float passedTime1;      //0x10
    float passedTime2;     //0x14
    float totalTime;    //0x18
};


class Minecraft {
    char pad_0000[0xD0];
public:
    Timer* simTimer;
    Timer* realTimer;
    void setTimer(float timer){
        simTimer->tps = timer;
    }
};

class MinecraftGame;
class BlockSource;

class Options {
public:
    int getPlayerViewPerspective();
};


class ClientInstance {
public:
    char pad_0000[0xA8];
    MinecraftGame* minecraftGame;     //0xA8
    Minecraft* minecraft;    //0xB0
public:
    LocalPlayer* getLocalPlayer();
    BlockSource* getRegion();
    GuiData* getGuiData();
    void handleDestroyOrAttackButtonPress();
    void handleBuildOrInteractButtonPress();
    Options* getOptions();
};


#endif //HOPECLIENT_CLIENTINSTANCE_H
