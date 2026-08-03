#include "Inventory.h"
#include "../Memory/GameData.h"
#include "../Utils/Logger.h"
#include <stdint.h>
#include "../Memory/GameData.h"

void avoidCrash(int i){
    i++;
}

int ItemStack::fromDescriptor(NetworkItemStackDescriptor* descriptor,BlockPalette* palette,bool usingNetId){

    __asm__ __volatile__ (
            "MOV x8, %0\n\t"
            "MOV x0, %1\n\t"
            "MOV x1, %2\n\t"
            "MOV x9, %3\n\t"
            "MOV W2, %w4\n\t"
            "BLR x9\n\t"
            :
            : "r" (reinterpret_cast<uint64_t>(this)),
              "r" (reinterpret_cast<uint64_t>(descriptor)),
              "r" (reinterpret_cast<uint64_t>(palette)),
              "r" (reinterpret_cast<uint64_t>(GameData::base+0xAD40FBC)),
              "r" (usingNetId)
            : "x9","memory"
            );

    int i =0;
    i++;
    avoidCrash(i);

    return 0;

}


int Item::getMaxDamage() {
    return CallVFunc<44,int>(this);
}

VarIntDataOutput::VarIntDataOutput() {
    mStream = new BinaryStream();
//    vTable = GameData::base+0xC430DC8;
}
