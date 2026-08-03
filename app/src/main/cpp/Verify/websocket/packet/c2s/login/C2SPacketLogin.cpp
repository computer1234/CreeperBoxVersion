#include "websocket/packet/c2s/login/C2SPacketLogin.h"

namespace team::cool::client::websocket::packet::c2s::login {

C2SPacketLogin::C2SPacketLogin(const std::string& username, const std::string& password,
                               const std::string& productType, const std::string& productVersion,
                               const std::string& other, const std::string& hardwareId,
                               const std::map<std::string, std::string>& hardwareMap)
    : username(username), password(password), productType(productType),
      productVersion(productVersion), other(other), hardwareId(hardwareId),
      hardwareMap(hardwareMap) {}

void C2SPacketLogin::write(ByteBuf& buf) {
    buf.writeString(username);
    buf.writeString(password);
    buf.writeString(productType);
    buf.writeString(productVersion);
    buf.writeString(other);
    buf.writeString(hardwareId);
    buf.writeMap(hardwareMap);
}

} // namespace team::cool::client::websocket::packet::c2s::login

