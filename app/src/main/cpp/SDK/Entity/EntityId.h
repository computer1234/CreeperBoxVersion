#ifndef SORAMC_ENTITYID_H
#define SORAMC_ENTITYID_H

#include "EntityIdTraits.h"
#include <concepts>
#include <cstdint>
#include "../../Include/entt/entity/entity.hpp"

template <>
class entt::entt_traits<EntityId> : public entt::basic_entt_traits<EntityIdTraits> {
public:
    static constexpr entity_type page_size = 2048;
};

class EntityId : public entt::entt_traits<EntityId> {
public:
    entity_type mRawId{};

    template <std::integral T>
    requires(!std::is_same_v<std::remove_cvref_t<T>, bool>)
    [[nodiscard]] constexpr EntityId(T rawId) : mRawId(static_cast<entity_type>(rawId))
    {
    } // NOLINT

    [[nodiscard]] constexpr bool isNull() const { return *this == entt::null; }

    [[nodiscard]] constexpr operator entity_type() const noexcept {
        return mRawId;
    }

};



#endif //SORAMC_ENTITYID_H
