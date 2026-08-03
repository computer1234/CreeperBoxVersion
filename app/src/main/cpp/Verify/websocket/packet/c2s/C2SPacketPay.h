#pragma once

#include "websocket/packet/Packet.h"
#include <string>

namespace team::cool::client::websocket::packet::c2s {

class C2SPacketPay : Packet {
private:
    std::string card;

public:
    C2SPacketPay(std::string card);

    void write(ByteBuf& buf) override;
    int packetId() override { return 5; }
};

}