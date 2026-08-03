#include "Skin.h"
#include "../Memory/GameData.h"

void SerializedSkin::write(BinaryStream *binaryStream) {
    using Fn = void (__fastcall*)(SerializedSkin*,BinaryStream*);
    reinterpret_cast<Fn>(GameData::base+0x4F60D3C)(this,binaryStream);
}

