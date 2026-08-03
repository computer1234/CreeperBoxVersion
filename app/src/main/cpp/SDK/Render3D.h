#ifndef HOPECLIENT_RENDER3D_H
#define HOPECLIENT_RENDER3D_H
#include <stack>
#include "Math.h"
#include "../Memory/GameData.h"


class LevelRendererPlayer{
private:
    char padding0000[0x9BC];
public:
    Vec3 cameraPos;                //0x9BC
    Vec3 targetCameraPos;                 //0x9C8
    void _ResetArea(){
        using Fn = std::string (__fastcall*)(void*);
        reinterpret_cast<Fn>(GameData::base+0x68E098C)(this);
    }
};


class LevelRenderer {
    char padding0000[0x4B0];
public:
    LevelRendererPlayer* levelRendererPlayer;     //0x4B0
};

class Actor;
class ActorRenderData{
public:
    Actor* actor;
};

class Matrix {
public:
    float m[16];
};


class MatrixStack {
public:
    std::stack<Matrix, std::deque<Matrix>> stack;
    char padding0030[0x18];
};


namespace mce {
    class Camera {
    public:
        MatrixStack viewMatrixStack;
        MatrixStack modelMatrixStack;
        MatrixStack projectionMatrixStack;
    };
};


class ScreenContext{
public:
    int xm;
    int ym;
    float partialTicks;  //0x8
    int padding;
    void* renderContext;
    mce::Camera* camera;      //0x18
};





#endif
