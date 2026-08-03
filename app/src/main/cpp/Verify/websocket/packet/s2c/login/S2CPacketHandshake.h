#pragma once

#include "websocket/packet/Packet.h"
#include <string>
#include <vector>

namespace team::cool::client::websocket::packet::s2c::login {

class S2CPacketHandshake : public Packet {
private:
    std::string salt;
    std::vector<uint8_t> serverPublic;

public:
    void read(ByteBuf& buf) override;
    void handle() override;
};

} // namespace team::cool::client::websocket::packet::s2c::login

