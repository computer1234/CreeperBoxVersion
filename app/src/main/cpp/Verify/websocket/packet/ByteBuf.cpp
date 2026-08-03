#include "websocket/packet/ByteBuf.h"
#include <stdexcept>
#include <cstring>

namespace team::cool::client::websocket::packet {

void ByteBuf::writeByte(uint8_t value) {
    encrypt::Byte encValue(value);
    writeEncryptByte(encValue);
}

void ByteBuf::writeBytes(const std::vector<uint8_t>& bytes) {
    encrypt::Bytes<0x3A,0x5B> encValue(
            reinterpret_cast<const char*>(bytes.data()),
            bytes.size()
    );
    writeEncryptBytes(encValue);
}

void ByteBuf::writeInt(int32_t value) {
    writeLong(static_cast<int64_t>(value));
}

void ByteBuf::writeLong(int64_t value) {
    encrypt::Long64 encValue(value);
    writeEncryptLong64(encValue);
}

void ByteBuf::writeDouble(double value) {
    uint64_t bits;
    std::memcpy(&bits, &value, sizeof(double));
    writeLong(static_cast<int64_t>(bits));
}

void ByteBuf::writeString(const std::string& str) {
    encrypt::String<0x3A,0x5B> encValue(str.c_str(), str.length());
    writeEncryptString(encValue);
}

void ByteBuf::writeMap(const std::map<std::string, std::string>& obj) {
    writeInt(static_cast<int32_t>(obj.size()));
    for (const auto& pair : obj) {
        writeString(pair.first);
        writeString(pair.second);
    }
}

uint8_t ByteBuf::readByte() {
    encrypt::Byte encValue = readEncryptByte();
    return encValue.get();
}

std::vector<uint8_t> ByteBuf::readBytes(size_t length) {
    encrypt::Bytes<0x3A,0x5B> encValue = readEncryptBytes(length);

    size_t actualSize = encValue.size();
    const unsigned char* decryptedData = encValue.udata();

    std::vector<uint8_t> result(decryptedData, decryptedData + actualSize);

    return result;
}

int32_t ByteBuf::readInt() {
    int64_t value = readLong();
    return static_cast<int32_t>(value);
}

int64_t ByteBuf::readLong() {
    encrypt::Long64 encValue = readEncryptLong64();
    return encValue.get();
}

double ByteBuf::readDouble() {
    int64_t bits = readLong();
    double value;
    std::memcpy(&value, &bits, sizeof(double));
    return value;
}

std::string ByteBuf::readString() {
    encrypt::String<0x3A,0x5B> encValue = readEncryptString();
    return encValue.c_str();
}

void ByteBuf::writeUnsignedVarInt64(uint64_t value) {
    while (value > 0x7F) {
        buffer.push_back((uint8_t)((value & 0x7F) | 0x80));
        value >>= 7;
    }
    buffer.push_back((uint8_t)value);
}

bool ByteBuf::read(void* target, size_t num) {
    if (!num) return true;
    if (readerIndex + num > buffer.size()) {
        return false;
    }
    std::memcpy(target, buffer.data() + readerIndex, num);
    readerIndex += num;
    return true;
}

uint64_t ByteBuf::readUnsignedVarInt64() {
    uint64_t value = 0;
    int shift = 0;

    while (true) {
        unsigned char byte;
        auto result = this->read(&byte, 1);
        if (!result) throw std::runtime_error("Failed to read varint : read failed");
        value |= (static_cast<uint64_t>(byte) & 0x7F) << shift;
        if (!(byte & 0x80)) break;
        shift += 7;
        if (shift >= 64) throw std::runtime_error("Failed to read varint : out of range");
    }
    return value;
}

void ByteBuf::writeEncryptString(encrypt::String<0x3A,0x5B>& value) {
    uint64_t size = value.size();
    writeUnsignedVarInt64(size);
    char* encrypted = value.encrypt();
    buffer.insert(buffer.end(), encrypted, encrypted + size);
}

encrypt::String<0x3A,0x5B> ByteBuf::readEncryptString() {
    uint64_t size = readUnsignedVarInt64();
    if (readerIndex + size > buffer.size())
        throw std::runtime_error("readEncryptString: buffer underflow");

    encrypt::String<0x3A,0x5B> value(
            reinterpret_cast<const char*>(&buffer[readerIndex]),
            size
    );
    value.setEncrypted(true);
    readerIndex += size;
    return value;
}

void ByteBuf::writeEncryptBytes(encrypt::Bytes<0x3A,0x5B>& value) {
    uint64_t size = value.size();
    char* encrypted = value.encrypt();
    buffer.insert(buffer.end(), encrypted, encrypted + size);
}

encrypt::Bytes<0x3A,0x5B> ByteBuf::readEncryptBytes(int32_t size) {
    if (readerIndex + size > buffer.size())
        throw std::runtime_error("readEncryptBytes: buffer underflow");

    encrypt::Bytes<0x3A,0x5B> value(
            reinterpret_cast<const char*>(&buffer[readerIndex]),
            size
    );
    value.setEncrypted(true);
    readerIndex += size;
    return value;
}

void ByteBuf::writeEncryptLong64(encrypt::Long64& value) {
    writeUnsignedVarInt64(value.getRandomIV());
    writeUnsignedVarInt64(value.getHash());
    writeUnsignedVarInt64(value.getEncryptedData());
}

encrypt::Long64 ByteBuf::readEncryptLong64() {
    uint64_t iv = readUnsignedVarInt64();
    uint32_t hash = readUnsignedVarInt64();
    uint64_t encrypted = readUnsignedVarInt64();
    return encrypt::Long64(encrypted, iv, hash);
}

void ByteBuf::writeEncryptByte(encrypt::Byte& value) {
    writeUnsignedVarInt64(value.getRandomIV());
    writeUnsignedVarInt64(value.getHash());
    writeUnsignedVarInt64(value.getEncryptedData());
}

encrypt::Byte ByteBuf::readEncryptByte() {
    uint64_t iv = readUnsignedVarInt64();
    uint32_t hash = readUnsignedVarInt64();
    uint64_t encrypted = readUnsignedVarInt64();
    return encrypt::Byte(encrypted, iv, hash);
}
} // namespace team::cool::client::websocket::packet