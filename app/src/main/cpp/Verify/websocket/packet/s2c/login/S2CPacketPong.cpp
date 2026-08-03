#include "websocket/packet/s2c/login/S2CPacketPong.h"
#include "util/crypt/ChaCha20.h"
#include "websocket/handler/MessageHandler.h"
#include "Main.h"
#include "websocket/packet/c2s/login/C2SPacketPing.h"
#include "../../../../../Utils/Logger.h"
#include <memory>

namespace team::cool::client::websocket::packet::s2c::login {

int64_t S2CPacketPong::nowTime = 0;

void S2CPacketPong::read(ByteBuf& buf) {
    message = buf.readString();
    int32_t nonceLen = buf.readInt();
    nonce = buf.readBytes(nonceLen);
    int32_t payloadLen = buf.readInt();
    payload = buf.readBytes(payloadLen);
}

void S2CPacketPong::handle() {
    std::vector<uint8_t> decrypted = util::crypt::ChaCha20::decrypt(nonce, payload);
    std::string decryptedMessage(decrypted.begin(), decrypted.end());
    
    if (decryptedMessage != message) {
        return;
    }
    
    LOGI("收到回响: %s", message.c_str());
    
    if (message == "Ciallo world!") {
        auto pingPacket = std::make_shared<c2s::login::C2SPacketPing>("Fuck world.");
        send(pingPacket);
        return;
    }
    
    if (message.length() >= 2 && message.substr(0, 2) == "tk") {
        std::string token = message.substr(2);
        handler::MessageHandler::connect("ws://" + Main::URL + "/message", token);
    }
}

} // namespace team::cool::client::websocket::packet::s2c::login

