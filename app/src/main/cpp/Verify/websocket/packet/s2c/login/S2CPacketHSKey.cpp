#include "websocket/packet/s2c/login/S2CPacketHSKey.h"
#include "util/crypt/HS256.h"
#include "websocket/handler/PacketHandler.h"
#include "Main.h"
#include "Config.h"
#include "websocket/packet/c2s/login/C2SPacketLogin.h"
#include "../../../../../Utils/Logger.h"
#include <map>
#include <memory>

namespace team::cool::client::websocket::packet::s2c::login {

void S2CPacketHSKey::read(ByteBuf& buf) {
    key = buf.readString();
}

void S2CPacketHSKey::handle() {
    LOGI("收到HMac SHA256 Key: %s", key.c_str());

    util::crypt::HS256::setKey(key);
    
    std::map<std::string, std::string> hardwareMap;
    auto loginPacket = std::make_shared<c2s::login::C2SPacketLogin>(
        Config::getUsername(),
        Config::getPassword(),
        Config::getProductType(),
        Config::getProductVersion(),
        Config::getOther(),
        Config::getHardwareId(),
        Config::getHardwareMap()
    );
    handler::PacketHandler::send(Main::loginHandler, loginPacket);
}

} // namespace team::cool::client::websocket::packet::s2c::login

