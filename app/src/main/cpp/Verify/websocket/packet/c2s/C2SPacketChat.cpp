#include "websocket/packet/c2s/C2SPacketChat.h"

namespace team::cool::client::websocket::packet::c2s {

C2SPacketChat::C2SPacketChat(const std::string& message) : message(message) {}

void C2SPacketChat::write(ByteBuf& buf) {
    buf.writeString(message);
}

} // namespace team::cool::client::websocket::packet::c2s

