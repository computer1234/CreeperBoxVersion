#pragma once

#include "websocket/packet/Packet.h"

namespace team::cool::client::websocket::packet::s2c::login {

class S2CPacketLogin : public Packet {
private:
    uint8_t type;
    std::string message;
    uint8_t state;
public:
    void read(ByteBuf& buf) override;
};

} // namespace team::cool::client::websocket::packet::s2c::login

