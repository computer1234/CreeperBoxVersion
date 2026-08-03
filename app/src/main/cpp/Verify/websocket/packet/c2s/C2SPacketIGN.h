#pragma once

#include "websocket/packet/Packet.h"
#include <string>

namespace team::cool::client::websocket::packet::c2s {

class C2SPacketIGN : public Packet {
private:
    std::string ign;

public:
    explicit C2SPacketIGN(const std::string& ign);
    
    void write(ByteBuf& buf) override;
    int packetId() override { return 3; }
};

} // namespace team::cool::client::websocket::packet::c2s

