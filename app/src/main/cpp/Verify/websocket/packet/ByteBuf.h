#pragma once

#include "encrypt/Byte.h"
#include "encrypt/Bytes.h"
#include "encrypt/String.h"
#include "encrypt/Long64.h"
#include <utility>
#include <vector>
#include <cstdint>
#include <string>
#include <map>
#include <cstring>

namespace team::cool::client::websocket::packet {

class ByteBuf {
private:
    std::vector<uint8_t> buffer;
    size_t readerIndex = 0;

public:
    ByteBuf() = default;
    explicit ByteBuf(size_t initialCapacity) : buffer(initialCapacity) {}
    explicit ByteBuf(std::vector<uint8_t> _buffer) : buffer(std::move(_buffer)), readerIndex(0) {}
    
    void writeByte(uint8_t value);
    void writeBytes(const std::vector<uint8_t>& bytes);
    void writeInt(int32_t value);
    void writeLong(int64_t value);
    void writeDouble(double value);
    void writeString(const std::string& str);
    void writeMap(const std::map<std::string, std::string>& obj);

    bool read(void* target, size_t num);
    void writeEncryptString(encrypt::String<0x3A,0x5B>& value);
    encrypt::String<0x3A,0x5B> readEncryptString();
    void writeEncryptBytes(encrypt::Bytes<0x3A,0x5B>& value);
    encrypt::Bytes<0x3A,0x5B> readEncryptBytes(int32_t size);
    void writeEncryptLong64(encrypt::Long64& value);
    encrypt::Long64 readEncryptLong64();
    void writeEncryptByte(encrypt::Byte& value);
    encrypt::Byte readEncryptByte();

    void writeUnsignedVarInt64(uint64_t value);
    uint64_t readUnsignedVarInt64();

    uint8_t readByte();
    std::vector<uint8_t> readBytes(size_t length);
    int32_t readInt();
    int64_t readLong();
    double readDouble();
    std::string readString();
    
    void setReaderIndex(size_t index) { readerIndex = index; }
    size_t getReaderIndex() const { return readerIndex; }
    size_t readableBytes() const { return buffer.size() - readerIndex; }
    
    const std::vector<uint8_t>& getBuffer() const { return buffer; }
    std::vector<uint8_t>& getBuffer() { return buffer; }
    
    void clear() {
        buffer.clear();
        readerIndex = 0;
    }
};

} // namespace team::cool::client::websocket::packet

