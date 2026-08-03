#pragma once

#include "websocket/packet/Packet.h"
#include <vector>
#include <cstdint>

namespace team::cool::client::websocket::packet::s2c {

/**
 * 云变量类型枚举
 */
enum class CloudVarType : int32_t {
    PLAYER_NORMAL_TICK = 0,              // oPlayer_normalTick
    NETWORK_SYSTEM_SEND_INTERNAL = 1,    // oNetworkSystem_sendInternal
    NETWORK_CONNECTION_RECEIVE = 2,      // oNetworkConnection_receivePacket
    LEVEL_RENDERER_RENDER = 3,           // oLevelRenderer_renderLevel
    BINARY_STREAM_CTOR = 4               // BinaryStream::BinaryStream
};

/**
 * 云变量结构体
 * type: 0-4 类型标识
 * address: ARM64 函数指针地址
 */
struct CloudVariable {
    int32_t type;
    int64_t address;
};

/**
 * 云变量响应包
 * 在验证成功后接收，包含远程 Hook/调用的函数地址
 */
class S2CPacketCloudVar : public Packet {
public:
    // 全局云变量列表，供其他模块使用
    static std::vector<CloudVariable> variables;
    
    // 云变量偏移（默认0xDEADBEEF，验证成功后填入正确值）
    static uintptr_t offsetPlayerNormalTick;
    static uintptr_t offsetNetworkSendInternal;
    static uintptr_t offsetNetworkReceivePacket;
    static uintptr_t offsetLevelRendererRender;
    static uintptr_t offsetBinaryStreamCtor;
    
    ~S2CPacketCloudVar() override;  // 显式析构函数，确保 vtable 生成
    
    void read(ByteBuf& buf) override;
    void handle() override;
    
private:
    // 异或解密密钥（与服务端相同）
    static constexpr uint8_t XOR_KEY = 0x5A;
};

} // namespace team::cool::client::websocket::packet::s2c
