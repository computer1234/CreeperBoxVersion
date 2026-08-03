#pragma once

#include "websocket/packet/Packet.h"
#include <string>

namespace team::cool::client::websocket::packet::c2s {

class C2SPacketChat : public Packet {
private:
    std::string message;

public:
    explicit C2SPacketChat(const std::string& message);
    
    void write(ByteBuf& buf) override;
    int packetId() override { return 1; }
};

} // namespace team::cool::client::websocket::packet::c2s

