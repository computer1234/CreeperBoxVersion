#include "JNIHelpers.h"
#include "SDK/Actor.h"

// ItemStack Native Methods
jshort ItemStack_getAuxValue(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->auxValue;
    }
    return 0;
}

jboolean ItemStack_isValid(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    return item->isValid();
}

jint ItemStack_getCount(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getCount();
    }
    return 0;
}

jint ItemStack_getMaxStackSize(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getItem()->maxStackSize;
    }
    return 0;
}

void ItemStack_setMaxStackSize(JNIEnv *env, jobject thiz, jint size) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        item->getItem()->maxStackSize = size;
    }
}

jstring ItemStack_getName(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return env->NewStringUTF(item->getItem()->name.c_str());
    }
    return env->NewStringUTF("minecraft:air");
}

jboolean ItemStack_isBlock(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->isBlock();
    }
    return false;
}

jint ItemStack_getItemId(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getItem()->itemId;
    }
    return 0;
}

jobject ItemStack_getNetworkData(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    static jclass cls = nullptr;
    static jmethodID method = nullptr;
    if (cls == nullptr) {
        jclass localCls = env->FindClass("helper/creeperbox/sdk/InstanceGenerator");
        cls = (jclass)env->NewGlobalRef(localCls);
        env->DeleteLocalRef(localCls);
        method = env->GetStaticMethodID(cls,"generatorItemData",
                                        "([B)Lorg/cloudburstmc/protocol/bedrock/data/inventory/ItemData;");
    }
    BinaryStream* bin = new BinaryStream();
    item->getNetworkItemStackDescriptor().write(bin);
    jobject ret = env->CallStaticObjectMethod(cls,method, convertStringToByteArray(env,bin->buffer));
    delete bin;
    return ret;
}

jint ItemStack_getMaxDamage(JNIEnv *env, jobject thiz) {
    ItemStack* item = getInstance<ItemStack*>(env,thiz);
    if(item->isValid()){
        return item->getItem()->getMaxDamage();
    }
    return 0;
}

static const JNINativeMethod gItemStackMethods[] = {
        {"a", "()S", (void*)ItemStack_getAuxValue},
        {"b", "()Z", (void*)ItemStack_isValid},
        {"c", "()I", (void*)ItemStack_getCount},
        {"d", "()I", (void*)ItemStack_getMaxStackSize},
        {"e", "(I)V", (void*)ItemStack_setMaxStackSize},
        {"f", "()Ljava/lang/String;", (void*)ItemStack_getName},
        {"g", "()Z", (void*)ItemStack_isBlock},
        {"h", "()I", (void*)ItemStack_getItemId},
        {"i", "()Lorg/cloudburstmc/protocol/bedrock/data/inventory/ItemData;", (void*)ItemStack_getNetworkData},
        {"j", "()I", (void*)ItemStack_getMaxDamage}
};

void register_Item(JNIEnv* env) {
    jclass itemClass = env->FindClass("helper/creeperbox/sdk/item/ItemStack");
    env->RegisterNatives(itemClass, gItemStackMethods, sizeof(gItemStackMethods) / sizeof(gItemStackMethods[0]));
}
