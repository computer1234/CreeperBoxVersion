#pragma once

#include "websocket/packet/Packet.h"
#include <string>
#include <vector>

namespace team::cool::client::websocket::packet::c2s::login {

class C2SPacketPing : public Packet {
private:
    std::string message;
    std::vector<uint8_t> nonce;
    std::vector<uint8_t> payload;

public:
    explicit C2SPacketPing(const std::string& message);
    
    void write(ByteBuf& buf) override;
    void handle() override;
    int packetId() override { return -123; }
};

} // namespace team::cool::client::websocket::packet::c2s::login

