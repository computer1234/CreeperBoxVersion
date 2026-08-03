#include "Packet.h"
#include "../Memory/GameData.h"
#include "../Utils/Logger.h"
#include "../Memory/Hooks.h"
#include "../Verify/websocket/packet/s2c/S2CPacketCloudVar.h"

using namespace team::cool::client::websocket::packet::s2c;

NetworkItemStackDescriptor::NetworkItemStackDescriptor(ItemStack *item) {
    using Fn = void (__fastcall*)(NetworkItemStackDescriptor*,ItemStack*);
    reinterpret_cast<Fn>(GameData::base+0xACF09F0)(this,item);
}

void NetworkItemStackDescriptor::write(BinaryStream *binaryStream) {
    using Fn = void (__fastcall*)(NetworkItemStackDescriptor*,BinaryStream*);
    reinterpret_cast<Fn>(GameData::base+0x7DC074C)(this,binaryStream);
}


NetworkItemStackDescriptor::NetworkItemStackDescriptor() {
    memset(this, 0, sizeof(NetworkItemStackDescriptor));
    vTable = GameData::base+0xF5947E0;
}

BinaryStream::BinaryStream() {
    memset(this,0,sizeof(this));
    using Fn = void (__fastcall*)(BinaryStream*);
    //这里是测试版本
//    reinterpret_cast<Fn>(GameData::base + 0x4F5E83C)(this);
    // 使用云变量偏移（默认0xDEADBEEF，验证成功后才有正确值）
    reinterpret_cast<Fn>(GameData::base + S2CPacketCloudVar::offsetBinaryStreamCtor)(this);
}

//void BinaryStream::reset() {
//    using Fn = void (__fastcall*)(BinaryStream*);
//    reinterpret_cast<Fn>(GameData::base+0x4319C74)(this);
//}


void avoidCrash2(int i){
    i++;
}

void BinaryStream::readItem(NetworkItemStackDescriptor* descriptor) {

    __asm__ __volatile__ (
            "MOV x8, %0\n\t"
            "MOV x0, %1\n\t"
            "MOV x1, %2\n\t"
            "MOV x9, %3\n\t"
            "BLR x9\n\t"
            :
            : "r" (reinterpret_cast<uint64_t>(descriptor)),
              "r" (reinterpret_cast<uint64_t>(this)),
              "r" (reinterpret_cast<uint64_t>(descriptor)),
              "r" (reinterpret_cast<uint64_t>(GameData::base+0x7A42C68))
            : "x9","memory"
            );

    int i =0;
    i++;
    avoidCrash2(i);

}

void CustomWritePacket::write(BinaryStream *stream) {
    *stream->buffer = *data;
}

CustomWritePacket::~CustomWritePacket() {
    delete data;
    data = nullptr;
}


CustomReadPacket::~CustomReadPacket() {
    delete data;
    data = nullptr;
}
