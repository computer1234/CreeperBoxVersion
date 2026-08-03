#include "websocket/packet/c2s/C2SPacketKeepalive.h"
#include "util/crypt/SecureRandom.h"

namespace team::cool::client::websocket::packet::c2s {

void C2SPacketKeepalive::write(ByteBuf& buf) {
    buf.writeDouble(id);
}

void C2SPacketKeepalive::handle() {
    id = util::crypt::SecureRandom::randomDouble();
}

} // namespace team::cool::client::websocket::packet::c2s

