#ifndef SORAMC_ENTITYCONTEXT_H
#define SORAMC_ENTITYCONTEXT_H

#include "../../Include/entt/entity/registry.hpp"
#include "EntityId.h"
#include "EntityRegistry.h"

class EntityContext {
public:
    EntityRegistry& mRegistry;
    entt::basic_registry<EntityId>& mEnttRegistry;
    EntityId mEntity;

    [[nodiscard]] inline entt::basic_registry<EntityId>& getRegistry()
    {
        return mEnttRegistry;
    }

    [[nodiscard]] inline entt::basic_registry<EntityId> const& getRegistry() const
    {
        return mEnttRegistry;
    }

    template <typename Component>
    [[nodiscard]] inline Component& get()
    {
        return mEnttRegistry.get<Component>(mEntity);
    }

    template <typename Component>
    [[nodiscard]] inline Component* try_get()
    {
        return mEnttRegistry.try_get<Component>(mEntity);
    }


    template <typename Component>
    [[nodiscard]] inline Component const& get() const
    {
        return mEnttRegistry.get<Component>(mEntity);
    }

    template <typename Component>
    inline void insert() const
    {
        if (mEnttRegistry.any_of<Component>(mEntity)) {
            return;
        }
        mEnttRegistry.emplace<Component>(mEntity);
    }
};


#endif //SORAMC_ENTITYCONTEXT_H
