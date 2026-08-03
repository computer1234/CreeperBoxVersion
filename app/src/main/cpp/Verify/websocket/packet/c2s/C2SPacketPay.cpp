#include "websocket/packet/c2s/C2SPacketPay.h"

namespace team::cool::client::websocket::packet::c2s {

C2SPacketPay::C2SPacketPay(std::string card)
        : card(card) {}

void C2SPacketPay::write(team::cool::client::websocket::packet::ByteBuf &buf) {
    buf.writeString(card);
}

}