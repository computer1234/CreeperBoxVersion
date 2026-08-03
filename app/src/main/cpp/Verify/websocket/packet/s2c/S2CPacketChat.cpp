#include "websocket/packet/s2c/S2CPacketChat.h"
#include "../../../../Utils/Logger.h"

namespace team::cool::client::websocket::packet::s2c {

void S2CPacketChat::read(ByteBuf& buf) {
    type = buf.readByte();
    message = buf.readString();
}

void S2CPacketChat::handle() {
    // TODO: 方块人打印信息
    LOGI("%d: %s", static_cast<int>(type), message.c_str());
}

} // namespace team::cool::client::websocket::packet::s2c

