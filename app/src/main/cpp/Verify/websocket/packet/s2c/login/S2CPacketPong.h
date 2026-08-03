#pragma once

#include "websocket/packet/Packet.h"
#include <string>
#include <vector>

namespace team::cool::client::websocket::packet::s2c::login {

class S2CPacketPong : public Packet {
private:
    std::string message;
    std::vector<uint8_t> nonce;
    std::vector<uint8_t> payload;

public:
    static int64_t nowTime;
    
    void read(ByteBuf& buf) override;
    void handle() override;
};

} // namespace team::cool::client::websocket::packet::s2c::login

