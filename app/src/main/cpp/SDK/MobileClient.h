#pragma once
#include <string>
#include "../Memory/GameData.h"

class MobileClient {
public:
    char padding0000[0x10];
    unsigned int mFrames;
    float mFps;
    float mFrameTime;
    int mPadding001C;
    char padding0020[0x20];
    std::string mLoginUid;    //0x40
    char padding0058[0x90];
    std::string mRoomSid;     //0xE8

    static MobileClient* singleton(){
        using getMobileClient = MobileClient* (__fastcall*)();
        return reinterpret_cast<getMobileClient>(GameData::base+0xC4107B4)();
    }
};

static_assert(offsetof(MobileClient,mLoginUid)==0x40);
