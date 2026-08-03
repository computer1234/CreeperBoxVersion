#pragma once

#include "websocket/packet/Packet.h"
#include <string>

namespace team::cool::client::websocket::packet::c2s {

class C2SPacketRegister : Packet {
private:
    std::string username;
    std::string password;
    std::string card;

public:
    C2SPacketRegister(std::string username, std::string password, std::string card);

    void write(ByteBuf& buf) override;
    int packetId() override { return 4; }
};

}