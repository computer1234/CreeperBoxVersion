#ifndef COMPONENT_H
#define COMPONENT_H
#include "Math.h"
#include <vector>
#include <unordered_map>


struct ActorRotationComponent {
    Vec2 headRot;
    Vec2 headRotPrev;
};

struct ActorHeadRotationComponent  {
    float yaw;
    float yawPrev;
};

struct MobBodyRotationComponent {
    float yaw;
    float yawPrev;
};

struct StateVectorComponent {
    Vec3 pos;
    Vec3 posPrev;
    Vec3 posDelta;   //motion
};



struct ActorGameTypeComponent{
    int gameType;                    //Player::setPlayerGameType
};

struct MaxAutoStepComponent {
    float stepHeight;
};

enum AbilityType : int{
    Build,
    Mine,
    DoorAndSwitches,
    OpenContainers,
    AttackPlayers,
    AttackMobs,
    OP,
    Teleport,
    Invulnerable,
    Flying,
    MayFly,
    InstaBuild,
    Lightning,
    FlySpeed,
    WalkSpeed,
    Mute,
    Worldbuilder,
    NoClip,
    PrivilegedBuilder
};

enum class AbilitiesIndex : signed char {
    Invalid           = -1,
    Build             = 0,
    Mine              = 1,
    DoorsAndSwitches  = 2,
    OpenContainers    = 3,
    AttackPlayers     = 4,
    AttackMobs        = 5,
    OperatorCommands  = 6,
    Teleport          = 7,
    Invulnerable      = 8,
    Flying            = 9,
    MayFly            = 10,
    Instabuild        = 11,
    Lightning         = 12,
    FlySpeed          = 13,
    WalkSpeed         = 14,
    Muted             = 15,
    WorldBuilder      = 16,
    NoClip            = 17,
    PrivilegedBuilder = 18,
    VerticalFlySpeed  = 19,
};

enum class CommandPermissionLevel : unsigned char {
    Any           = 0,
    GameDirectors = 1,
    Admin         = 2,
    Host          = 3,
    Owner         = 4,
    Internal      = 5,
};

enum class PlayerPermissionLevel : signed char {
    Visitor  = 0,
    Member   = 1,
    Operator = 2,
    Custom   = 3,
};

struct PermissionsHandler {
    CommandPermissionLevel mCommandPermissions;
    int padding;
    char mNeteaseExtra[0x10];
    char mPlayerPermissions[0x50];
};

class Ability {
public:
    enum class Type : unsigned char {
        Invalid = 0,
        Unset   = 1,
        Bool    = 2,
        Float   = 3,
    };

    enum class Options : unsigned char {
        None                        = 0,
        NoSave                      = 1 << 0,
        CommandExposed              = 1 << 1,
        PermissionsInterfaceExposed = 1 << 2,
    };

    union Value {
    public:
        bool mBoolVal;
        float mFloatVal;
    };

    static Ability& INVALID_ABILITY() {
        static Ability a{Type::Invalid,false, Options::None};
        return a;
    }

public:
    Ability::Type mType;
    Ability::Value mValue;
    Ability::Options mOptions;
};

class Abilities {
public:
    std::array<Ability,19> mAbilities;
};

struct AbilitiesComponent {
    PermissionsHandler mPermissions;
    std::array<Abilities,6> mLayers;

    Ability& getAbility(AbilitiesIndex val){
        if (mLayers.empty() || val > AbilitiesIndex::VerticalFlySpeed) {
            return Ability::INVALID_ABILITY();
        }
        for (auto& layer : mLayers) {
            auto& ability = layer.mAbilities.at((size_t)val);
            if (ability.mType != Ability::Type::Unset) {
                return ability;
            }
        }
        return Ability::INVALID_ABILITY();
    }

    bool getAbilityBool(int val){
        return getAbility(static_cast<AbilitiesIndex>(val)).mValue.mBoolVal;
    }

    float getAbilityFloat(int val){
        return getAbility(static_cast<AbilitiesIndex>(val)).mValue.mFloatVal;
    }

    void setAbilityBool(int val,bool value){
        getAbility(static_cast<AbilitiesIndex>(val)).mValue.mBoolVal = value;
    }

    void setAbilityFloat(int val,float value){
        getAbility(static_cast<AbilitiesIndex>(val)).mValue.mFloatVal = value;
    }

};

struct ActorDefinitionIdentifierComponent {
    char pad_0x0000[0x68];
    std::string name;        //0x68
    char pad_0x0080[0x8];
};


#pragma pack(pop)
struct MoveInputComponent {
    bool isSneakDown;    //0x0
    bool isSneakToggleDown;    //0x1
    bool isFlyDownSlowDown;    //0x2
    bool isFlyUpSlowDown;      //0x3
    bool isSelectedBlock;       //0x4
    bool isAscendScaffoldingDown;     //0x5
    bool isDescendScaffoldingDown;    //0x6
    bool isJumpDown;     //0x7
    bool isSprintDown;    //0x8
    bool isUpLeftDown;       //0x9
    bool isUpRightDown;      //0xA
    bool isUpDown;     //0xB
    bool isDownDown;     //0xC
    bool isLeftDown;     //0xD
    bool isRightDown;       //0xE
    bool isFlyingAscendDown;       //飞行上升    0xF
    bool isFlyingDescendDown;             //飞行下降      0x10
    char pad_0x0011[0x33];
    float moveSide;       //0x44
    float moveForward;        //0x48
    char pad_0x004C[0x7C];
};


struct FallDistanceComponent {
    float fallDistance;
};

struct RuntimeIDComponent {
    long long runtimeID;
};

struct ActorUniqueIDComponent {
    long long uniqueID;
};

struct BlockMovementSlowdownMultiplierComponent {
    Vec3 slowDownFactor;
};

struct Tick{
    long long tick;
};

struct ActorTypeComponent{
    int type;
};

struct MobJumpComponent {
    Vec3 lastJumpPos;
    int padding;
    int jumpDelay;
};

struct MobEffectInstance {
    unsigned int mModId;
    bool mIsRefreshed;
    unsigned int mId;
    int mDuration;
    int mDurationEasy;
    int mDurationNormal;
    int mDurationHard;
    int mAmplifier;
    bool mDisplayOnScreenTextureAnimation;
    bool mAmbient;
    bool mNoCounter;
    bool mEffectVisible;
    char padding[92];
};

struct MobEffectsComponent {
    std::vector<MobEffectInstance> mMobEffects;
};

struct OnGroundFlagComponent {};

enum class RedefinitionMode : signed char {
    KeepCurrent        = 0,
    UpdateToNewDefault = 1,
};

typedef uint64_t HashType64;

class HashedString {
public:
    HashType64 mStrHash{};
    std::string mStr;
    mutable const HashedString* mLastMatch{};
};

class Attribute {
public:
    RedefinitionMode mRedefinitionMode;
    bool mSyncable;
    uint32_t mIDValue;
    HashedString mName;
};

class BaseAttributeMap;
class TemporalAttributeBuff;
class AttributeInstanceHandle;
class AttributeInstanceDelegate;
class AttributeModifier;
class Actor;

class AttributeInstance {
public:
    bool mPyMax;
    BaseAttributeMap *mAttributeMap;
    const Attribute* mAttribute;
    std::vector<AttributeModifier> mModifierList;
    std::vector<TemporalAttributeBuff> mTemporalBuffs;
    std::vector<AttributeInstanceHandle> mListeners;
    std::shared_ptr<AttributeInstanceDelegate> mDelegate;

    union {
        float mDefaultValues[3];
        struct {
            float mDefaultMinValue;
            float mDefaultMaxValue;
            float mDefaultValue;
        };
    };

    union {
        float mCurrentValues[3];
        struct {
            float mCurrentMinValue;
            float mCurrentMaxValue;
            float mCurrentValue;
        };
    };

public:
    virtual ~AttributeInstance() = default;
    virtual void tick(Actor* actor) {}
};

struct AttributeInstanceHandle {
    unsigned int mAttributeID;
};

struct AttributesComponent {
    std::unordered_map<uint32_t,AttributeInstance> mInstanceMap;
    std::vector<AttributeInstanceHandle> mDirtyAttributes;

    enum NeteaseAttribute : uint32_t {
        EMPTY,
        HEALTH,
        FOLLOW_RANGE,
        KNOCKBACK_RESISTANCE,
        MOVEMENT,
        UNDERWATER_MOVEMENT,
        LAVA_MOVEMENT,
        ATTACK_DAMAGE,
        ABSORPTION,
        LUCK,

        //Horse
        JUMP_STRENGTH,

        //Player
        HUNGER,
        SATURATION,
        EXHAUSTION,
        LEVEL,
        EXPERIENCE

    };
};

class ActorDataFlagComponent {
public:
    std::bitset<127> bitset;

    bool getStatusFlag(uint32_t flagId) const {
        if (flagId >= bitset.size()) {
            return false;
        }
        return bitset.test(flagId);
    }

    void setStatusFlag(uint32_t flagId, bool value) {
        if (flagId < bitset.size()) {
            bitset.set(flagId, value);
        }
    }
};


#endif
