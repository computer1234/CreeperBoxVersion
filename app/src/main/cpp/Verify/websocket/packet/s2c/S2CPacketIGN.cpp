#include "websocket/packet/s2c/S2CPacketIGN.h"
#include "Main.h"

namespace team::cool::client::websocket::packet::s2c {

void S2CPacketIGN::read(ByteBuf& buf) {
    type = buf.readByte();
    value1 = buf.readString();
    value2 = buf.readString();
}

void S2CPacketIGN::handle() {
    switch (type) {
        case 0:
            Main::inGameNames[value1] = value2;
            break;
        case 1: {
            auto it = Main::inGameNames.find(value1);
            if (it != Main::inGameNames.end()) {
                std::string username = it->second;
                Main::inGameNames.erase(it);
                Main::inGameNames[value2] = username;
            }
            break;
        }
        case 2:
            Main::inGameNames.erase(value1);
            break;
    }
}

} // namespace team::cool::client::websocket::packet::s2c

