
#ifndef GAMEMODE_H
#define GAMEMODE_H

#include "Actor.h"
#include "Math.h"

class LocalPlayer;
class ItemStack;
class Block;
class Actor;

class GameMode {

private:
    virtual u_int64_t destructorGameMode1();
    virtual u_int64_t destructorGameMode2();
public:
    virtual u_int64_t startDestroyBlock(Vec3i const &pos, unsigned char blockSide, bool &isDestroyedOut);
    virtual u_int64_t destroyBlock(Vec3i const &, unsigned char);
    virtual u_int64_t continueDestroyBlock(Vec3i const &, unsigned char blockSide, bool &isDestroyedOut);
    virtual u_int64_t stopDestroyBlock(Vec3i const &);
    virtual u_int64_t startBuildBlock(Vec3i const &, unsigned char);
    virtual u_int64_t buildBlock(Vec3i *, unsigned char, bool);
    virtual u_int64_t continueBuildBlock(Vec3i const &, unsigned char);
    virtual u_int64_t stopBuildBlock();
    virtual u_int64_t tick();

    virtual u_int64_t getPickRange(u_int64_t const &, bool);
    virtual u_int64_t useItem(ItemStack &);
    virtual void useItemOn(ItemStack &, Vec3i const &, char, Vec3 const &, Block const *);
    virtual u_int64_t interact(Actor &, Vec3 const &);
    virtual u_int64_t attack(Actor *);
    virtual u_int64_t releaseUsingItem();


    LocalPlayer *player;
    Vec3i destroyBlockPos;       //0x10
    int destroyFace;       //0x1C
    float oldDestroyProgress;     //0x20
    float destroyProgress;      //0x24
};



#endif //GAMEMODE_H
