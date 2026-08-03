#pragma once

#include "websocket/packet/Packet.h"
#include <memory>
#include <map>
#include <functional>

namespace team::cool::client::websocket::handler {

class PacketHandler {
private:
    static std::map<int8_t, std::function<std::shared_ptr<packet::Packet>()>> packetMap;

public:
    static void init();
    static std::shared_ptr<packet::Packet> packet(int8_t id);
    static void send(void* client, std::shared_ptr<packet::Packet> packet);
    static void send(void* client, const std::vector<uint8_t>& message);
};

} // namespace team::cool::client::websocket::handler

