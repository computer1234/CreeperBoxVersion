#include "websocket/packet/c2s/login/C2SPacketPing.h"
#include "util/Generator.h"
#include "util/crypt/ChaCha20.h"
#include "websocket/packet/s2c/login/S2CPacketPong.h"
#include "../../../../../Utils/Logger.h"
#include <chrono>

namespace team::cool::client::websocket::packet::c2s::login {

C2SPacketPing::C2SPacketPing(const std::string& message) : message(message) {}

void C2SPacketPing::write(ByteBuf& buf) {
    buf.writeString(message);
    buf.writeInt(static_cast<int32_t>(nonce.size()));
    buf.writeBytes(nonce);
    buf.writeInt(static_cast<int32_t>(payload.size()));
    buf.writeBytes(payload);
}

void C2SPacketPing::handle() {
    nonce = util::Generator::randomBytes(12);
    std::vector<uint8_t> messageBytes(message.begin(), message.end());
    payload = util::crypt::ChaCha20::encrypt(nonce, messageBytes);
    
    s2c::login::S2CPacketPong::nowTime = std::chrono::duration_cast<std::chrono::nanoseconds>(
        std::chrono::high_resolution_clock::now().time_since_epoch()
    ).count();

    LOGI("发送响应: %s", message.c_str());
}

} // namespace team::cool::client::websocket::packet::c2s::login

