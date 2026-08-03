#ifndef HOPECLIENT_PACKET_H
#define HOPECLIENT_PACKET_H
#include <string>
class ItemStack;

class ReadOnlyBinaryStream{

};



class NetworkItemStackDescriptor;

class BinaryStream : public ReadOnlyBinaryStream {
public:
    char padding000[0x40];
    std::string* buffer;   //0x40
public:
    char padding001[0x30];
    BinaryStream();
//    void reset();
    void readItem(NetworkItemStackDescriptor* descriptor);
};


class NetworkItemStackDescriptor {
public:
    long vTable;
    char padding0008[0xC];
    int count;
    char padding0018[0x100];
public:
    NetworkItemStackDescriptor(ItemStack* item);
    NetworkItemStackDescriptor();
    void write(BinaryStream* binaryStream);
};

enum PacketPriority {
    IMMEDIATE_PRIORITY,
    HIGH_PRIORITY,
    MEDIUM_PRIORITY,
    LOW_PRIORITY,
    NUMBER_OF_PRIORITIES
};

enum class Reliability : int {
    Reliable,
    ReliableOrdered,
    Unreliable,
    UnreliableSequenced
};


class Packet {
public:
    PacketPriority mPriority;
    Reliability mReliability;
private:
    char padding0000[0x100];
    virtual u_int64_t destructorPacket1() = 0;
    virtual u_int64_t destructorPacket2() = 0;
public:
    virtual int getId() = 0;
    virtual std::string getName() = 0;
    virtual void checkSize(uint64_t packetSize, bool receiverIsServer) = 0;
    virtual void write(BinaryStream* stream) = 0;
    virtual void read(BinaryStream* stream) = 0;
    virtual bool disallowBatching() {
        return false;
    }
    virtual bool isValid() const {
        return true;
    }
    virtual void read(ReadOnlyBinaryStream&) {};
};


class CustomWritePacket : public Packet{
private:
    std::string* data;
public:
    bool callEvent;
public:
    CustomWritePacket(std::string* data) : data(data){
    }
    ~CustomWritePacket();

    u_int64_t destructorPacket1() override {
        return 0;
    }
    u_int64_t destructorPacket2() override {
        return 0;
    }
    int getId() override {
        return 0;
    };

    std::string getName() override {
        return "CustomWritePacket";
    }

    void checkSize(uint64_t packetSize, bool receiverIsServer) override {
    }
    void write(BinaryStream* stream) override;

    void read(BinaryStream *stream) override {
    }
};

class CustomReadPacket {
public:
    CustomReadPacket(std::string* data,bool callEvent) : data(data) , callEvent(callEvent){
    }
    ~CustomReadPacket();

    std::string* data;
    bool callEvent;
};

class LoopbackPacketSender {
private:
    virtual void* destructorLoopbackPacketSender1();
    virtual void* destructorLoopbackPacketSender2();
public:
    virtual void* send(Packet* packet);
    virtual void* sendToServer(Packet* packet);
    virtual void* sendToClients(const void* networkIdentifier, const Packet* packet);
    virtual void* sendToClient(const void* networkIdentifier, const Packet* packet, int a4);
    virtual void* sendToClients();
    virtual void* sendBroadcast(const Packet*packet);
    virtual void* sendBroadcast(const void* networkIdentifier, int a3, const Packet* packet);
    virtual void* flush(void* networkIdentifier);
};


#endif
