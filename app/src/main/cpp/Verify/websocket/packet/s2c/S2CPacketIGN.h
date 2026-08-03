#pragma once

#include "websocket/packet/Packet.h"
#include <string>

namespace team::cool::client::websocket::packet::s2c {

class S2CPacketIGN : public Packet {
private:
    uint8_t type;
    std::string value1;
    std::string value2;

public:
    void read(ByteBuf& buf) override;
    void handle() override;
};

} // namespace team::cool::client::websocket::packet::s2c

