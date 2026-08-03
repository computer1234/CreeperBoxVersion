#include "websocket/packet/s2c/login/S2CPacketLogin.h"
#include "../../../../../Utils/Logger.h"
#include "websocket/packet/c2s/login/C2SPacketHandshake.h"
#include "Main.h"
#include "Config.h"
#include "../../../../../Memory/GameData.h"
#include <memory>

namespace team::cool::client::websocket::packet::s2c::login {

void S2CPacketLogin::read(ByteBuf& buf) {
    type = buf.readByte();
    message = buf.readString();
    state = buf.readByte();

    if (state == 1) {
        // 验证成功，读取剩余时间
        int64_t remainingTime = buf.readLong(); // 剩余时间（毫秒），-1表示永久
        
        LOGI("收到登录回响，类型: %d, 信息: %s，尝试与服务端握手", static_cast<int>(type), message.c_str());
        LOGI("卡密验证成功: %s, 剩余时间: %lld ms", Config::getUsername().c_str(), static_cast<long long>(remainingTime));

        // 保存剩余时间到 GameData（供其他模块使用）
        GameData::verifyTime = remainingTime;

        // 设置状态为正在验证，等待 ECDH 握手完成
        Main::setStatus(VerifyStatus::AUTHENTICATING);

        auto handshakePacket = std::make_shared<c2s::login::C2SPacketHandshake>();
        send(handshakePacket);
    } else {
        LOGI("收到登录回响，类型: %d, 信息: %s", static_cast<int>(type), message.c_str());
        Main::setStatus(VerifyStatus::FAILED_AUTH, message);
    }
}

} // namespace team::cool::client::websocket::packet::s2c::login
