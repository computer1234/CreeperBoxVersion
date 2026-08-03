#include "websocket/packet/s2c/S2CPacketCloudVar.h"
#include "../../../../Utils/Logger.h"
#include "../../../../Memory/GameData.h"
#include "../../../../Memory/Hooks.h"
#include "../../../../Include/Arm64InlineHook/arm64_inlinehook.h"

// 原函数指针（外部定义在Hooks.cpp中，全局命名空间）
extern void* (*oPlayer_normalTick)(void*);
extern void* (*oNetworkSystem_sendInternal)(void*,void*,void*,void*);
extern bool (*oNetworkConnection_receivePacket)(void*,void*,void*,void*,int);
extern void* (*oLevelRenderer_renderLevel)(void*,void*,void*);

namespace team::cool::client::websocket::packet::s2c {

// 静态变量定义
std::vector<CloudVariable> S2CPacketCloudVar::variables;

// 云变量偏移（默认无效地址，验证成功后填入正确值）
uintptr_t S2CPacketCloudVar::offsetPlayerNormalTick = 0xDEADBEEF;
uintptr_t S2CPacketCloudVar::offsetNetworkSendInternal = 0xDEADBEEF;
uintptr_t S2CPacketCloudVar::offsetNetworkReceivePacket = 0xDEADBEEF;
uintptr_t S2CPacketCloudVar::offsetLevelRendererRender = 0xDEADBEEF;
uintptr_t S2CPacketCloudVar::offsetBinaryStreamCtor = 0xDEADBEEF;

// 析构函数定义（作为 key function 确保 vtable 生成）
S2CPacketCloudVar::~S2CPacketCloudVar() = default;

void S2CPacketCloudVar::read(ByteBuf& buf) {
    // 读取加密数据长度
    int32_t dataLen = buf.readInt();
    
    // 读取加密数据
    std::vector<uint8_t> encryptedData = buf.readBytes(dataLen);
    
    // 异或解密
    for (size_t i = 0; i < encryptedData.size(); i++) {
        encryptedData[i] ^= XOR_KEY;
    }
    
    // 从解密后的数据中解析云变量
    ByteBuf dataBuf(encryptedData);
    
    int32_t count = dataBuf.readInt();
    LOGI("收到 %d 个云变量", count);
    
    variables.clear();
    variables.reserve(count);
    
    for (int32_t i = 0; i < count; i++) {
        CloudVariable var;
        var.type = dataBuf.readInt();
        var.address = dataBuf.readLong();
        variables.push_back(var);
        
        LOGI("云变量 [%d]: type=%d, offset=0x%llX", i, var.type, static_cast<unsigned long long>(var.address));
    }
}

void S2CPacketCloudVar::handle() {
    uintptr_t base = GameData::base;
    Arm64InlineHook* hooker = Arm64InlineHook::getInstance();
    
    // 将云变量偏移存储到静态成员并主动执行Hook
    for (const auto& var : variables) {
        uintptr_t offset = static_cast<uintptr_t>(var.address);
        switch (static_cast<CloudVarType>(var.type)) {
            case CloudVarType::PLAYER_NORMAL_TICK:
                offsetPlayerNormalTick = offset;
                LOGI("主动Hook: oPlayer_normalTick @ 0x%llX", static_cast<unsigned long long>(base + offset));
                hooker->hookFunction(reinterpret_cast<void*>(base + offset),
                                     (void*)(&Hooks::Player_normalTick),
                                     (void**)&oPlayer_normalTick);
                break;

            case CloudVarType::NETWORK_SYSTEM_SEND_INTERNAL:
                offsetNetworkSendInternal = offset;
                LOGI("主动Hook: oNetworkSystem_sendInternal @ 0x%llX", static_cast<unsigned long long>(base + offset));
                hooker->hookFunction(reinterpret_cast<void*>(base + offset),
                                     (void*)(&Hooks::NetworkSystem_sendInternal),
                                     (void**)&oNetworkSystem_sendInternal);
                break;

            case CloudVarType::NETWORK_CONNECTION_RECEIVE:
                offsetNetworkReceivePacket = offset;
                LOGI("主动Hook: oNetworkConnection_receivePacket @ 0x%llX", static_cast<unsigned long long>(base + offset));
                hooker->hookFunction(reinterpret_cast<void*>(base + offset),
                                     (void*)(&Hooks::NetworkConnection_receivePacket),
                                     (void**)&oNetworkConnection_receivePacket);
                break;

            case CloudVarType::LEVEL_RENDERER_RENDER:
                offsetLevelRendererRender = offset;
                LOGI("主动Hook: oLevelRenderer_renderLevel @ 0x%llX", static_cast<unsigned long long>(base + offset));
                hooker->hookFunction(reinterpret_cast<void*>(base + offset),
                                     (void*)(&Hooks::LevelRenderer_renderLevel),
                                     (void**)&oLevelRenderer_renderLevel);
                break;

            case CloudVarType::BINARY_STREAM_CTOR:
                offsetBinaryStreamCtor = offset;
                LOGI("云变量: BinaryStream::BinaryStream offset = 0x%llX", static_cast<unsigned long long>(offset));
                // 此偏移用于SDK/Packet.cpp中的函数调用，不需要Hook
                break;

            default:
                LOGW("未知云变量类型: %d", var.type);
                break;
        }
    }

    LOGI("主动Hook完成，共 %zu 个", variables.size());
}

} // namespace team::cool::client::websocket::packet::s2c

