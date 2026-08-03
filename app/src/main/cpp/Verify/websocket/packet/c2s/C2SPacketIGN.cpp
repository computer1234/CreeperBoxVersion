#include "websocket/packet/c2s/C2SPacketIGN.h"

namespace team::cool::client::websocket::packet::c2s {

C2SPacketIGN::C2SPacketIGN(const std::string& ign) : ign(ign) {}

void C2SPacketIGN::write(ByteBuf& buf) {
    buf.writeString(ign);
}

} // namespace team::cool::client::websocket::packet::c2s

