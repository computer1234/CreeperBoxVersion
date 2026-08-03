#ifndef HOPECLIENT_BLOCKSOURCE_H
#define HOPECLIENT_BLOCKSOURCE_H


#include "Math.h"
#include "Actor.h"
#include <cstdint>

enum MaterialType : unsigned int {
    Air,
    Dirt,
    Wood,
    Stone,
    Metal,
    Water,
    Lava,
    Leaves,
    Plant,
    SolidPlant,
    ReplaceablePlant,
    Sponge,
    Cloth,
    Bed,
    Fire,
    Sand,
    Decoration,
    Glass,
    Explosive,
    Ice,
    PackedIce,
    TopSnow,
    Snow,
    PowderSnow,
    Amethyst,
    Cactus,
    Clay,
    Vegetable,
    Portal,
    Cake,
    Web,
    RedstoneWire,
    Carpet,
    BuildableGlass,
    Slime,
    Piston,
    Allow,
    Deny,
    Netherwart,
    StoneDecoration,
    Bubble,
    Egg,
    SoftEgg,
    Barrier,
    Coral,
    DecorationSolid,
    Dripstone,
    ReinforcedStone,
    Sculk,
    SculkVein,
    ClientRequestPlaceholder,
    StructureVoid,
    Root,
    SurfaceTypeTotal,
    Any
};

class Material {
public:
    int mType;       //0x0
    bool mNeverBuildable;    //0x4
    bool mLiquid;    //0x5
    bool mBlocksMotion;     //0x6
    bool mBlocksPrecipitation;      //0x7
    bool mSolid;    //0x8
    bool mSuperHot;     //0x9
};

struct DataLoadHelper {};

class Level;

class CompoundTag;


class BlockActor {
public:
    char padding[0x54];
    Vec3i blockPos;
private:
    virtual u_int64_t destructorBlockActor1();
    virtual u_int64_t destructorBlockActor2();
public:
    virtual void load(Level&,CompoundTag const&,DataLoadHelper &);
    virtual void save(CompoundTag &);
};

class Block;

class BlockSource {
private:
    virtual u_int64_t destructorBlockSource1();
    virtual u_int64_t destructorBlockSource2();
public:
    virtual Block* getBlock(int, int, int);
    virtual Block* getBlock(Vec3i const &);
    virtual Block* getBlock(Vec3i const &, int);
    virtual BlockActor* getBlockEntity(Vec3i const &);
    virtual Block* getExtraBlock(Vec3i const &);
    virtual Block* getLiquidBlock(Vec3i const &);
    virtual bool hasBlock(Vec3i const &);
    virtual bool containsAnyLiquid(AABB const &);
    virtual bool containsMaterial(AABB const &, MaterialType);
    virtual void isUnderWater(Vec3 const& pos);
    virtual Material* getMaterial(Vec3i const &);
    virtual Material* getMaterial(int, int, int);
};


class BlockLegacy {
private:
    char pad_0x0000[0x130];
    std::string name;
public:
    bool getCollisionShape(AABB* collShapeOut, Block* block, BlockSource* blockSource, const Vec3i* pos,Actor* actor);
    std::string getNameSpace();
};


class Block {
private:
    virtual u_int64_t destructorBlock1();
    virtual u_int64_t destructorBlock2();
    virtual int getRenderLayer();
public:
    char pad_0x0008[0xD0];
    int padding;    //0xD8
    unsigned short mData;       //0xDC
    BlockLegacy* legacy;    //0xE0
    char pad_0x00E8[0xBC];
    int runtimeID;        //0x1A4
};

#endif //HOPECLIENT_BLOCKSOURCE_H
