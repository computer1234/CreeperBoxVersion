#ifndef HOPECLIENT_INVENTORY_H
#define HOPECLIENT_INVENTORY_H
#include <functional>
#include "Packet.h"
#include <map>

class Item {
public:
    char pad_0x0000[0x98];
    int16_t maxStackSize;  // 0x98
    int16_t itemId;     //0x9B
    std::string name;    //0xA0

    int getMaxDamage();
};

enum TagType : unsigned char
{
    TAG_End = 0x0,
    TAG_Byte = 0x1,
    TAG_Short = 0x2,
    TAG_Int = 0x3,
    TAG_Long = 0x4,
    TAG_Float = 0x5,
    TAG_Double = 0x6,
    TAG_Byte_Array = 0x7,
    TAG_String = 0x8,
    TAG_List = 0x9,
    TAG_Compound = 0xA,
    TAG_Int_Array = 0xB
};


class Tag {
public:
    virtual void destructor1(){}
    virtual void destructor2() {}
    virtual void deleteChildren() {}
    virtual void write(class BasicDataOutput&) const {}
    virtual void load(class IDataInput&)  {}
    virtual void writeScriptData(class IDataOutput&) {}
    virtual void loadScriptData(IDataOutput&) {};
    virtual std::string toString() const { return "UNKNOWN"; }
    virtual TagType getId() const {return TAG_End;}
    virtual bool equals(Tag const&) const { return false; }
    virtual void print(std::string const&,class BasicPrintStream&) const {}
    virtual void print(BasicPrintStream&) const {}
    virtual std::unique_ptr<Tag> copy() const { return std::make_unique<Tag>(); }
    virtual uint64_t hash() const { return -1; }
};



class EndTag : public Tag {

};

class ByteTag : public Tag {
public:
    unsigned char data;
};


class ShortTag : public Tag {
public:
    short data;
};

class IntTag : public Tag {
public:
    int data;
};

class Int64Tag : public Tag {
public:
    int64_t data;
};

class FloatTag : public Tag {
public:
    float data;
};

class DoubleTag : public Tag {
public:
    long double data;
};

class StringTag : public Tag {
public:
    std::string data;
};

class TagMemoryChunk {
public:
    u_int64_t elements;
    u_int64_t size;
    std::unique_ptr<unsigned char [0]> buffer;
};

class ByteArrayTag : public Tag {
public:
    TagMemoryChunk data;
};

class ListTag : public Tag {
public:
    std::vector<std::unique_ptr<Tag>> list;
    char type[1];
};

class IntArrayTag : public Tag {
public:
    TagMemoryChunk data;
};

class CompoundTagVariant;


class IDataOutput {
public:
    uintptr_t vTable;
};

class VarIntDataOutput : public IDataOutput {
public:
    BinaryStream *mStream;

    VarIntDataOutput();

    ~VarIntDataOutput(){
        delete mStream;
    }
};


class CompoundTag {
public:
    using TagMap = std::map<std::string, CompoundTagVariant>;
    TagMap mTags;

    virtual void destructor1();
    virtual void destructor2();
    virtual void write(IDataOutput& dos);
    virtual void load(IDataInput& docs);
};


class CompoundTagVariant {
public:
    std::variant<EndTag,ByteTag,ShortTag,IntTag,Int64Tag,FloatTag,DoubleTag,ByteArrayTag,StringTag,ListTag,CompoundTag,IntArrayTag> tagStorage;
};


class Block;

class BlockPalette;

class ItemStack {      //size 0xA0
private:
    uintptr_t **vTable;  //0x0000
public:
    Item **item;     //0x08
    CompoundTag* userData;   //0x10
    Block* block;      //0x18
    unsigned short auxValue;    //0x20
    char padding0022[0x58];
    long pickUpTime;            //0x80
    char padding0088[0x100];        //0x88


    bool isValid() {
        return this->item != nullptr && *this->item != nullptr;
    }

    int getCount(){
        return getNetworkItemStackDescriptor().count;
    }

    NetworkItemStackDescriptor getNetworkItemStackDescriptor(){
        return NetworkItemStackDescriptor(this);
    }

    bool isBlock(){
        return block != nullptr;
    }

    inline Item *getItem() {
        return *this->item;
    }

    ItemStack(){
        memset(this, 0, sizeof(ItemStack));
    }

    int fromDescriptor(NetworkItemStackDescriptor* descriptor, BlockPalette* palette, bool usingNetId);
};


enum class ContainerType : signed char{
    NONE = -9,
    INVENTORY = -1,
    CONTAINER,
    WORKBENCH,
    FURNACE,
    ENCHANTMENT,
    BREWING_STAND,
    ANVIL,
    DISPENSER,
    DROPPER,
    HOPPER,
    CAULDRON,
    MINECART_CHEST,
    MINECART_HOPPER,
    HORSE,
    BEACON,
    STRUCTURE_EDITOR,
    TRADE,
    COMMAND_BLOCK,
    JUKEBOX,
    ARMOR,
    HAND,
    COMPOUND_CREATOR,
    ELEMENT_CONSTRUCTOR,
    MATERIAL_REDUCER,
    LAB_TABLE,
    LOOM,
    LECTERN,
    GRINDSTONE,
    BLAST_FURNACE,
    SMOKER,
    STONECUTTER,
    CARTOGRAPHY,
    HUD,
    JIGSAW_EDITOR,
    SMITHING_TABLE,
    CHEST_BOAT,
    DECORATED_POT,
    CRAFTER
};

class BlockSource;
class Player;
class Vec3;


class Container {

public:
    ContainerType containerType;           //0x8
    ContainerType gameplayContainerType;     //0x9
    char pad_0x0002[0x7E];
    std::string name;    //0x88
    bool hasCustomName;
    unsigned int runtimeID;
public:
    virtual void Destructor1();
    virtual void Destructor2();
    virtual void init();
    virtual void serverInitItemStackIds(int, int, std::function<void(int, ItemStack const &)>);
    virtual void addContentChangeListener(uint64_t *);
    virtual void addRemovedListener(uint64_t *);
    virtual void removeContentChangeListener(uint64_t *);
    virtual void idk();
    virtual ItemStack* getItemStack(int);
    virtual bool hasRoomForItem(ItemStack const &);
    virtual void addItem(ItemStack*);
    virtual void addItemWithForceBalance(ItemStack &);
    virtual void addItemToFirstEmptySlot(ItemStack*);
    virtual void setItem(int, ItemStack const &);
    virtual void setItemWithForceBalance(int, ItemStack const &, bool);
    virtual void removeItem(int, int);
    virtual void removeAllItems(void);
    virtual void removeAllItemsWithForceBalance(void);
    virtual void containerRemoved(void);
    virtual void dropSlotContent(void);
    virtual void dropContents(BlockSource &, Vec3 const &, bool);
    virtual int getContainerSize(void);
    virtual int getMaxStackSize(void);
    virtual void startOpen(Player &);
    virtual void stopOpen(Player &);
    virtual int getSlotCopies(void);
    virtual int getSlots(void);
    virtual int getEmptySlotsCount(void);
    virtual int getItemCount(ItemStack const &);
    virtual void findFirstSlotForItem(ItemStack const &);
    virtual void canPushInItem(int, int, ItemStack const &);
    virtual void canPullOutItem(int, int, ItemStack const &);
    virtual void setContainerChanged(int);
    virtual void setContainerMoved(void);
    virtual void setCustomName(std::string const &);
};

class FillingContainer : public Container{
public:

};

class Inventory : public FillingContainer{

};




class PlayerInventory {
public:
    char pad_0x0000[0x10];
    int selectedHotbarSlot;
    char pad_0x0014[0x11C];  //0x0014
    Inventory* container;  //0x00120
};



#endif //HOPECLIENT_INVENTORY_H
