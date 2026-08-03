#include "JNIHelpers.h"
#include "SDK/Actor.h"

// PlayerInventory Native Methods
jobject PlayerInventory_getContainer(JNIEnv *env, jobject thiz) {
    Inventory* container = getInstance<PlayerInventory*>(env,thiz)->container;
    jclass cls = env->FindClass("helper/creeperbox/sdk/inventory/Container");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(container));
    return obj;
}

jint PlayerInventory_getSelectedHotbarSlot(JNIEnv *env, jobject thiz) {
    return getInstance<PlayerInventory*>(env,thiz)->selectedHotbarSlot;
}

void PlayerInventory_setSelectedHotbarSlot(JNIEnv *env, jobject thiz, jint slot) {
    getInstance<PlayerInventory*>(env,thiz)->selectedHotbarSlot = slot;
}

// Container Native Methods
jint Container_getType(JNIEnv *env, jobject thiz) {
    return static_cast<jint>(getInstance<Container *>(env, thiz)->containerType);
}

jint Container_getRuntimeID(JNIEnv *env, jobject thiz) {
    return getInstance<Container *>(env, thiz)->runtimeID;
}

jboolean Container_hasCustomName(JNIEnv *env, jobject thiz) {
    return getInstance<Container *>(env, thiz)->hasCustomName;
}

jstring Container_getName(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<Container *>(env, thiz)->name.c_str());
}

jint Container_getSize(JNIEnv *env, jobject thiz) {
    return getInstance<Container *>(env, thiz)->getContainerSize();
}

jobject Container_getItemStack(JNIEnv *env, jobject thiz, jint slot) {
    static ItemStack emptyItemStack;

    Container* container = getInstance<Container*>(env, thiz);
    ItemStack* item = container ? container->getItemStack(slot) : &emptyItemStack;

    jclass cls = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    return env->NewObject(cls, env->GetMethodID(cls, "<init>", "(J)V"),
                          reinterpret_cast<long>(item));
}

static const JNINativeMethod gPlayerInventoryMethods[] = {
        {"a", "()Lhelper/creeperbox/sdk/inventory/Container;", (void*)PlayerInventory_getContainer},
        {"b", "()I", (void*)PlayerInventory_getSelectedHotbarSlot},
        {"c", "(I)V", (void*)PlayerInventory_setSelectedHotbarSlot}
};

static const JNINativeMethod gContainerMethods[] = {
        {"a", "()I", (void*)Container_getType},
        {"b", "()I", (void*)Container_getRuntimeID},
        {"c", "()Z", (void*)Container_hasCustomName},
        {"d", "()Ljava/lang/String;", (void*)Container_getName},
        {"e", "()I", (void*)Container_getSize},
        {"f", "(I)Lhelper/creeperbox/sdk/item/ItemStack;", (void*)Container_getItemStack}
};

void register_Inventory(JNIEnv* env) {
    jclass piClass = env->FindClass("helper/creeperbox/sdk/inventory/PlayerInventory");
    env->RegisterNatives(piClass, gPlayerInventoryMethods, sizeof(gPlayerInventoryMethods) / sizeof(gPlayerInventoryMethods[0]));

    jclass containerClass = env->FindClass("helper/creeperbox/sdk/inventory/Container");
    env->RegisterNatives(containerClass, gContainerMethods, sizeof(gContainerMethods) / sizeof(gContainerMethods[0]));
}
