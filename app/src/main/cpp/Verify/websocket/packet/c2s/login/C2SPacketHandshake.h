#pragma once

#include "websocket/packet/Packet.h"

namespace team::cool::client::websocket::packet::c2s::login {

class C2SPacketHandshake : public Packet {
private:
    std::vector<uint8_t> clientPublic;

public:
    void write(ByteBuf& buf) override;
    void handle() override;
    int packetId() override { return -101; }
};

} // namespace team::cool::client::websocket::packet::c2s::login

