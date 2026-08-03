#include "BlockSource.h"
#include "../Utils/MemUtils.h"

bool BlockLegacy::getCollisionShape(AABB *collShapeOut, Block *block, BlockSource *blockSource,
                                    const Vec3i *pos,Actor* actor) {
    return CallVFunc<8, bool, AABB *, Block *, BlockSource *, const Vec3i *,Actor*>(this,
                                                                                    collShapeOut,
                                                                                    block,
                                                                                    blockSource,
                                                                                    pos,
                                                                                    actor);
}

std::string BlockLegacy::getNameSpace() {
    return name;
}

