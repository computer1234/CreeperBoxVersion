#include "websocket/handler/PacketHandler.h"
#include "websocket/packet/s2c/login/S2CPacketLogin.h"
#include "websocket/packet/s2c/login/S2CPacketHSKey.h"
#include "websocket/packet/s2c/login/S2CPacketHandshake.h"
#include "websocket/packet/s2c/login/S2CPacketPong.h"
#include "websocket/packet/s2c/S2CPacketChat.h"
#include "websocket/packet/s2c/S2CPacketKeepalive.h"
#include "websocket/packet/s2c/S2CPacketIGN.h"
#include "websocket/packet/s2c/S2CPacketCloudVar.h"
#include "websocket/packet/Packet.h"
#include "util/ByteUtil.h"
#include "util/DateTimeUtil.h"
#include "util/Generator.h"
#include "util/crypt/ChaCha20.h"
#include "util/crypt/HS256.h"
#include "websocket/handler/MessageHandler.h"
#include "websocket/handler/LoginHandler.h"
#include "../../../Utils/Logger.h"
#include <stdexcept>

namespace team::cool::client::websocket::handler {

std::map<int8_t, std::function<std::shared_ptr<packet::Packet>()>> PacketHandler::packetMap;

void PacketHandler::init() {
    packetMap[0] = []() { return std::make_shared<packet::s2c::login::S2CPacketLogin>(); };
    packetMap[-245] = []() { return std::make_shared<packet::s2c::login::S2CPacketHSKey>(); };
    packetMap[-101] = []() { return std::make_shared<packet::s2c::login::S2CPacketHandshake>(); };
    packetMap[-123] = []() { return std::make_shared<packet::s2c::login::S2CPacketPong>(); };
    
    packetMap[1] = []() { return std::make_shared<packet::s2c::S2CPacketChat>(); };
    packetMap[2] = []() { return std::make_shared<packet::s2c::S2CPacketKeepalive>(); };
    packetMap[3] = []() { return std::make_shared<packet::s2c::S2CPacketIGN>(); };
    packetMap[4] = []() { return std::make_shared<packet::s2c::S2CPacketCloudVar>(); };
}

std::shared_ptr<packet::Packet> PacketHandler::packet(int8_t id) {
    auto it = packetMap.find(id);
    if (it == packetMap.end()) {
        throw std::runtime_error("Unknown packet ID: " + std::to_string(id));
    }
    return it->second();
}

void PacketHandler::send(void* client, std::shared_ptr<packet::Packet> packet) {
    if (!client || !packet) return;
    
    packet::ByteBuf buf;
    packet::ByteBuf packetBuf;
    
    buf.writeByte(static_cast<uint8_t>(packet->packetId()));
    
    packet->setClient(client);
    packet->handle();
    packet->write(packetBuf);
    
    std::vector<uint8_t> data;
    MessageHandler* msgHandler = MessageHandler::getInstance();
    bool isMessageHandler = (msgHandler != nullptr && client == static_cast<void*>(msgHandler));
    
    if (isMessageHandler) {
        std::vector<uint8_t> nonce = util::Generator::randomBytes(12);
        data = util::crypt::ChaCha20::encrypt(nonce, packetBuf.getBuffer());
        buf.writeBytes(nonce);
    } else {
        data = packetBuf.getBuffer();
    }
    
    std::vector<uint8_t> payload = util::ByteUtil::merge(data, util::ByteUtil::longToBytes(util::DateTimeUtil::getTime()));
    std::string sign = util::crypt::HS256::signC2S(payload);
    
    buf.writeString(sign);
    buf.writeInt(static_cast<int32_t>(payload.size()));
    buf.writeBytes(payload);

    send(client, buf.getBuffer());
}

void PacketHandler::send(void* client, const std::vector<uint8_t>& message) {
    if (!client) return;

    MessageHandler* msgHandler = MessageHandler::getInstance();
    bool isMessageHandler = (msgHandler != nullptr && client == static_cast<void*>(msgHandler));


    if (isMessageHandler) {
        if (msgHandler && msgHandler->isOpen()) {
            msgHandler->send(message);
            return;
        }
    } else {
        LoginHandler* loginHandler = static_cast<LoginHandler*>(client);
        if (loginHandler->isOpen()) {
            loginHandler->send(message);
            return;
        }
    }

    LOGE("无法发送数据：客户端未连接或类型不匹配");
}

} // namespace team::cool::client::websocket::handler

