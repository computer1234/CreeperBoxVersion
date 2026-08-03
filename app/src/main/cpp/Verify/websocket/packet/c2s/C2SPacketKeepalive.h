#pragma once

#include "websocket/packet/Packet.h"

namespace team::cool::client::websocket::packet::c2s {

class C2SPacketKeepalive : public Packet {
private:
    double id = 0.0;

public:
    void write(ByteBuf& buf) override;
    void handle() override;
    int packetId() override { return 2; }
};

} // namespace team::cool::client::websocket::packet::c2s

