#pragma once

#include "websocket/packet/Packet.h"
#include <set>

namespace team::cool::client::websocket::packet::s2c {

class S2CPacketKeepalive : public Packet {
private:
    double id;

public:
    static std::set<double> ids;
    static int64_t lastActive;
    
    void read(ByteBuf& buf) override;
    void handle() override;
    static void reset();
};

} // namespace team::cool::client::websocket::packet::s2c

