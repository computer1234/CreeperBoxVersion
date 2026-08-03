#ifndef HOPECLIENT_ACTOR_H
#define HOPECLIENT_ACTOR_H


#include <cstdint>
#include <jni.h>
#include "../Utils/MemUtils.h"
#include "Component.h"
#include "GameMode.h"
#include "Inventory.h"
#include "Level.h"
#include "Skin.h"
#include "Entity/EntityContext.h"

class Level;


class Actor {
private:
    uintptr_t vTable;
public:
    EntityContext mEntityContext;
public:
    bool isAlive();
    void swing();
    void setSprinting(bool state);
    bool isClientSide();
    bool isInvisible();
    bool canSeeActor(Actor* other);
    std::string* getNameTag();
    std::string getNamespace();
    void setSneaking(bool state);

    template<typename T>
    bool hasComponent() const {
        const auto& registry = mEntityContext.getRegistry();
        return registry.all_of<T>(mEntityContext.mEntity);
    }

    ActorHeadRotationComponent* getActorHeadRotationComponent();
    MobBodyRotationComponent* getMobBodyRotationComponent();
    ActorGameTypeComponent* getActorGameTypeComponent();
    MaxAutoStepComponent* getMaxAutoStepComponent();
    AbilitiesComponent* getAbilitiesComponent();
    RuntimeIDComponent* getRuntimeIDComponent();
    ActorUniqueIDComponent* getActorUniqueIDComponent();
    BlockMovementSlowdownMultiplierComponent* getBlockMovementSlowdownMultiplierComponent();
    MobJumpComponent* getMobJumpComponent();

    Vec3 getMotion();
    Vec3 getPosPrev();
    Vec3 getPos();
    void setMotionX(float motionX);
    void setMotionY(float motionY);
    void setMotionZ(float motionZ);
    void setMotion(Vec3 motion);
    Vec2 getSize();
    void setSize(Vec2 size);


    AABB* getAABB();
    ActorRotationComponent* getActorRotationComponent();
    StateVectorComponent *getStateVectorComponent();
    bool isPlayer();

    void setStatusFlag(int flag,bool value);
    bool getStatusFlag(int flag);

    ItemStack* getItemInHand();
    ItemStack* getItemOffHand();
    ItemStack* getArmor(int slot);
    Level* getLevel();


    bool isValid();



    FallDistanceComponent* getFallDistanceComponent();
    bool isOnGround();
    void setOnGround(bool onGround);
    int getHealth();
    AttributeInstance* getAttribute(int index);
    int getEntityTypeId();
    Actor* getVehicle();
    void setPos(Vec3* pos);
    bool isPrimaryLocalPlayer();

    MobEffectsComponent* getEffect();
};



class Mob : public Actor {

};


class Player : public Mob{
public:
    PlayerInventory* getInventory();
    SerializedSkin* getSkin();
};



class LocalPlayer : public Player {
private:
    char padding[0x790-0x20];
public:
    int swingTick;
    MoveInputComponent* getMoveInputHandler();
    GameMode* getGameMode();
    void applyTurnDelta(Vec2 *viewAngles);
    void displayClientMessage(std::string const &);
    float getDestroyRate(Block* block);
};



#endif //HOPECLIENT_ACTOR_H
