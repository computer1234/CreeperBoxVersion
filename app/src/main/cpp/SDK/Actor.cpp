#include "../Utils/Logger.h"
#include "../Memory/GameData.h"
#include "../Memory/Hooks.h"
#include "Actor.h"
#include <optional>

bool Actor::isInvisible() {
    return CallVFunc<37, bool>(this);
}


void Actor::setSneaking(bool state) {
    CallVFunc<50,void,bool>(this,state);
}

bool Actor::isAlive() {
    return CallVFunc<53, bool>(this);
}


ItemStack *Actor::getItemInHand() {
    return CallVFunc<85, ItemStack *>(this);          //减3得到getArmor
}

void Actor::swing() {
    CallVFunc<128, void,int>( this,1);
}


void Actor::setSprinting(bool state) {
    CallVFunc<163, void, bool>(this, state);
}


void LocalPlayer::displayClientMessage(const std::string & msg) {
    CallVFunc<228,void,const std::string&,std::optional<std::string>>(this,msg,{});
}


GameMode *LocalPlayer::getGameMode() {
    return *reinterpret_cast<GameMode**>((uintptr_t)(this) + 0x17A0);
}

Level *Actor::getLevel() {
    return *reinterpret_cast<Level**>((uintptr_t)(this) + 0x240);
}

StateVectorComponent *Actor::getStateVectorComponent() {
    return *reinterpret_cast<StateVectorComponent**>((uintptr_t)(this) + 0x278);
}

AABB *Actor::getAABB() {
    return *reinterpret_cast<AABB**>((uintptr_t)(this) + 0x280);
}

ActorRotationComponent* Actor::getActorRotationComponent(){
    return *reinterpret_cast<ActorRotationComponent**>((uintptr_t)(this) + 0x288);
}


PlayerInventory *Player::getInventory() {
    return *reinterpret_cast<PlayerInventory**>((uintptr_t)(this) + 0xBE8);
}


SerializedSkin *Player::getSkin() {
    return *reinterpret_cast<SerializedSkin**>((uintptr_t)(this) + 0x17D0);
}


std::string* Actor::getNameTag() {
    using Fn = std::string*(__thiscall*)(Actor*);
    return reinterpret_cast<Fn>(GameData::base+0x9B111E4)(this);
}

bool Actor::canSeeActor(Actor *other) {
    using Fn = bool(__thiscall*)(Actor*,Actor*,int);
    return reinterpret_cast<Fn>(GameData::base+0x9B18C7C)(this,other,1);
}

ItemStack *Actor::getArmor(int index) {
    using Fn = ItemStack*(__thiscall*)(Actor*,int);
    return reinterpret_cast<Fn>(GameData::base+0x9B0C06C)(this,index);
}


ItemStack *Actor::getItemOffHand() {
    using Fn = ItemStack*(__thiscall*)(Actor*);
    return reinterpret_cast<Fn>(GameData::base+0x9B0C6A0)(this);
}

void Actor::setPos(Vec3* pos) {
    using Fn = void(__thiscall*)(void*,Vec3*);
    // 正确偏移: player + 0x8
    reinterpret_cast<Fn>(GameData::base+0x5CD1730)(reinterpret_cast<void*>((uintptr_t)this + 0x8), pos);
}

Actor *Actor::getVehicle() {
    using Fn = Actor*(__thiscall*)(Actor*);
    return reinterpret_cast<Fn>(GameData::base+0x9AF8E04)(this);
}

void LocalPlayer::applyTurnDelta(Vec2 *viewAngles) {
    using Turn = void(__thiscall*)(LocalPlayer*, Vec2*);
    reinterpret_cast<Turn>(GameData::base+0x5F49218)(this, viewAngles);
}



Vec2 Actor::getSize() {
    return getAABB()->size;
}

void Actor::setSize(Vec2 size) {
    getAABB()->size = size;
}


bool Actor::isPlayer() {
    return getEntityTypeId() == 319;
}

float LocalPlayer::getDestroyRate(Block *block) {
    return Hooks::oGameMode_getDestroyRate(this->getGameMode(),block);
}

auto Actor::isValid() -> bool
{
    return (uintptr_t)this && *(uintptr_t*)this;
}



ActorHeadRotationComponent *Actor::getActorHeadRotationComponent() {
    return mEntityContext.try_get<ActorHeadRotationComponent>();
}

MobBodyRotationComponent *Actor::getMobBodyRotationComponent() {
    return mEntityContext.try_get<MobBodyRotationComponent>();
}

ActorGameTypeComponent *Actor::getActorGameTypeComponent() {
    return  mEntityContext.try_get<ActorGameTypeComponent>();
}

MaxAutoStepComponent *Actor::getMaxAutoStepComponent() {
    return  mEntityContext.try_get<MaxAutoStepComponent>();
}

AbilitiesComponent *Actor::getAbilitiesComponent() {
    return mEntityContext.try_get<AbilitiesComponent>();
}

MoveInputComponent *LocalPlayer::getMoveInputHandler() {
    return mEntityContext.try_get<MoveInputComponent>();
}

std::string Actor::getNamespace() {
    return mEntityContext.try_get<ActorDefinitionIdentifierComponent>()->name;
}

RuntimeIDComponent *Actor::getRuntimeIDComponent() {
    return mEntityContext.try_get<RuntimeIDComponent>();
}

ActorUniqueIDComponent *Actor::getActorUniqueIDComponent() {
    return mEntityContext.try_get<ActorUniqueIDComponent>();
}

BlockMovementSlowdownMultiplierComponent *Actor::getBlockMovementSlowdownMultiplierComponent() {
    return mEntityContext.try_get<BlockMovementSlowdownMultiplierComponent>();
}

FallDistanceComponent* Actor::getFallDistanceComponent() {
    return mEntityContext.try_get<FallDistanceComponent>();
}

MobEffectsComponent *Actor::getEffect() {
    return mEntityContext.try_get<MobEffectsComponent>();
}


int Actor::getEntityTypeId() {
    return mEntityContext.try_get<ActorTypeComponent>()->type;
}

MobJumpComponent *Actor::getMobJumpComponent() {
    return mEntityContext.try_get<MobJumpComponent>();
}

AttributeInstance *Actor::getAttribute(int index) {
    AttributesComponent* attr = mEntityContext.try_get<AttributesComponent>();
    auto ins = attr->mInstanceMap.find(index);
    return &ins->second;
}

bool Actor::isOnGround() {
    return hasComponent<OnGroundFlagComponent>();
}


bool Actor::getStatusFlag(int flag) {
    return mEntityContext.try_get<ActorDataFlagComponent>()->getStatusFlag(flag);
}

void Actor::setStatusFlag(int flag, bool value) {
    mEntityContext.try_get<ActorDataFlagComponent>()->setStatusFlag(flag,value);
}

void Actor::setOnGround(bool onGround) {
    //Might No use
}


bool Actor::isClientSide() {
    return getLevel()->isClientSide();
}

int Actor::getHealth() {
    return static_cast<int>(getAttribute(1)->mCurrentValue);
}

Vec3 Actor::getMotion() {
    return getStateVectorComponent()->posDelta;
}

void Actor::setMotionX(float motionX) {
    getStateVectorComponent()->posDelta.x = motionX;
}

void Actor::setMotionY(float motionY) {
    getStateVectorComponent()->posDelta.y = motionY;
}

void Actor::setMotionZ(float motionZ) {
    getStateVectorComponent()->posDelta.z = motionZ;
}

void Actor::setMotion(Vec3 motion) {
    getStateVectorComponent()->posDelta = motion;
}

Vec3 Actor::getPosPrev() {
    return getStateVectorComponent()->posPrev;
}

Vec3 Actor::getPos() {
    return getStateVectorComponent()->pos;
}

bool Actor::isPrimaryLocalPlayer() {
    return this == GameData::localplayer;
}


