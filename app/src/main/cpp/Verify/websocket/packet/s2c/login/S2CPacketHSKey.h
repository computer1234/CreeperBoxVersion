#pragma once

#include "websocket/packet/Packet.h"
#include <string>

namespace team::cool::client::websocket::packet::s2c::login {

class S2CPacketHSKey : public Packet {
private:
    std::string key;

public:
    void read(ByteBuf& buf) override;
    void handle() override;
};

} // namespace team::cool::client::websocket::packet::s2c::login

