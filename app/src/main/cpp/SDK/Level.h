#ifndef HOPECLIENT_LEVEL_H
#define HOPECLIENT_LEVEL_H
#include "Math.h"
#include "Actor.h"
#include "Entity/EntityContext.h"

enum struct HitType : int {
    BLOCK = 0,
    ENTITY = 1,
    AIR = 3
};

class WeakEntityRef {
public:
    std::weak_ptr<EntityContext> handle;
    int entityID;
};


class HitResult {
public:
    Vec3 start;
    Vec3 end;
    HitType hitType;
    int8_t face;
    Vec3i hitBlock;
    Vec3 hitPos;
    WeakEntityRef mEntity;
};

class LoopbackPacketSender;

class UUID {
public:
    static UUID EMPTY;
    static size_t STRING_LENGTH;
private:
    uint64_t Data[2];

public:
    UUID() {
        Data[0] = 0;
        Data[1] = 0;
    }

    UUID(uint64_t mostSignificantBits, uint64_t leastSignificantBits) {

        Data[0] = mostSignificantBits;
        Data[1] = leastSignificantBits;
    }

    uint64_t getMostSignificantBits() const {
        return Data[0];
    }

    uint64_t getLeastSignificantBits() const {
        return Data[1];
    }


    bool operator<(const UUID& rhs) const {
        return std::make_tuple(Data[0], Data[1]) < std::make_tuple(rhs.Data[0], rhs.Data[1]);
    }

    bool operator==(const UUID& rhs) const {
        return (Data[0] == rhs.Data[0]) && (Data[1] == rhs.Data[1]);
    }

    bool operator!=(const UUID& rhs) const {
        return !(*this == rhs);
    }

    size_t getHash() const {
        return (getLeastSignificantBits() * 0x1F1F1F1F) ^ getMostSignificantBits();
    }

};


class PlayerListEntry {
public:
    long long uniqueID;
    UUID uuid;
    std::string name;
};

class Level {
public:
    LoopbackPacketSender* getPacketSender();
    HitResult* getHitResult();
    std::vector<Actor*> getRuntimeActorList();
    Tick* getCurrentTick();
    void forEachPlayer(std::function<bool (Player &)>);
    BlockPalette* getBlockPalette();
    bool isClientSide();
    std::unordered_map<UUID,PlayerListEntry>& getPlayerList();
};



#endif //HOPECLIENT_LEVEL_H
