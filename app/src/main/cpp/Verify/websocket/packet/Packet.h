#pragma once

#include "websocket/packet/ByteBuf.h"
#include <memory>

namespace team::cool::client::websocket::packet {

class Packet {
protected:
    void* client = nullptr; // WebSocket client pointer

public:
    virtual ~Packet() = default;
    
    void setClient(void* c) { client = c; }
    
    virtual void read(ByteBuf& buf) {}
    virtual void write(ByteBuf& buf) {}
    virtual void handle() {}
    
    virtual int packetId() { return 0; }
    
protected:
    void send(std::shared_ptr<Packet> packet);
};

} // namespace team::cool::client::websocket::packet

