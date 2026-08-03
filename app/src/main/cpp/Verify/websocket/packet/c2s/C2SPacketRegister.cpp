#include "websocket/packet/c2s/C2SPacketRegister.h"

namespace team::cool::client::websocket::packet::c2s {

C2SPacketRegister::C2SPacketRegister(std::string username, std::string password, std::string card)
    : username(username), password(password), card(card) {}

void C2SPacketRegister::write(team::cool::client::websocket::packet::ByteBuf &buf) {
    buf.writeString(username);
    buf.writeString(password);
    buf.writeString(card);
}

}