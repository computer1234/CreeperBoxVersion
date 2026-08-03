#pragma once

#include "websocket/packet/Packet.h"
#include <string>

namespace team::cool::client::websocket::packet::s2c {

class S2CPacketChat : public Packet {
private:
    uint8_t type;
    std::string message;

public:
    void read(ByteBuf& buf) override;
    void handle() override;
};

} // namespace team::cool::client::websocket::packet::s2c

