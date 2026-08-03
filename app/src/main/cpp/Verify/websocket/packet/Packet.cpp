#include "websocket/packet/Packet.h"
#include "websocket/handler/PacketHandler.h"
#include <memory>

namespace team::cool::client::websocket::packet {

void Packet::send(std::shared_ptr<Packet> packet) {
    if (packet) {
        handler::PacketHandler::send(client, packet);
    }
}

} // namespace team::cool::client::websocket::packet

