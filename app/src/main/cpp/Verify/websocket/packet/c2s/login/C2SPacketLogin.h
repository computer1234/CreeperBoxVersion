#pragma once

#include "websocket/packet/Packet.h"
#include <string>
#include <map>

namespace team::cool::client::websocket::packet::c2s::login {

class C2SPacketLogin : public Packet {
private:
    std::string username;
    std::string password;
    std::string productType;
    std::string productVersion;
    std::string other;
    std::string hardwareId;
    std::map<std::string, std::string> hardwareMap;

public:
    C2SPacketLogin(const std::string& username, const std::string& password,
                   const std::string& productType, const std::string& productVersion,
                   const std::string& other, const std::string& hardwareId,
                   const std::map<std::string, std::string>& hardwareMap);
    
    void write(ByteBuf& buf) override;
};

} // namespace team::cool::client::websocket::packet::c2s::login

