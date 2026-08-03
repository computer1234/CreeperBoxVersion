#include "JNIHelpers.h"
#include "SDK/Render2D.h"
#include "SDK/Render3D.h"

// ScreenView Native Methods
void ScreenView_exit(JNIEnv *env, jobject thiz) {
    getInstance<ScreenView*>(env,thiz)->currentController->tryExit();
}

jstring ScreenView_getName(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(getInstance<ScreenView*>(env,thiz)->visualTree->mRootControl->name.c_str());
}

// LevelRenderer Native Methods
void LevelRenderer_resetArea(JNIEnv *env, jobject thiz) {
    getInstance<LevelRenderer*>(env,thiz)->levelRendererPlayer->_ResetArea();
}

jobject LevelRenderer_getCameraPos(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LevelRenderer*>(env,thiz)->levelRendererPlayer->cameraPos);
}

jobject LevelRenderer_getTargetCameraPos(JNIEnv *env, jobject thiz) {
    return fromVec3(env,getInstance<LevelRenderer*>(env,thiz)->levelRendererPlayer->targetCameraPos);
}

// Camera Helper
jobject createMatrix(JNIEnv *env, float* m) {
    jfloatArray matrixArray = env->NewFloatArray(16);
    if (matrixArray == nullptr) return nullptr;
    env->SetFloatArrayRegion(matrixArray, 0, 16, m);
    jclass matrixClass = env->FindClass("helper/creeperbox/sdk/math/Matrix");
    if (matrixClass == nullptr) return nullptr;
    jmethodID constructor = env->GetMethodID(matrixClass, "<init>", "([F)V");
    if (constructor == nullptr) return nullptr;
    jobject matrixObject = env->NewObject(matrixClass, constructor, matrixArray);
    env->DeleteLocalRef(matrixArray);
    env->DeleteLocalRef(matrixClass);
    return matrixObject;
}

// Camera Native Methods
jobject Camera_getViewMatrix(JNIEnv *env, jobject thiz) {
    return createMatrix(env, getInstance<mce::Camera*>(env, thiz)->viewMatrixStack.stack.top().m);
}

jobject Camera_getModelMatrix(JNIEnv *env, jobject thiz) {
    return createMatrix(env, getInstance<mce::Camera*>(env, thiz)->modelMatrixStack.stack.top().m);
}

jobject Camera_getProjectionMatrix(JNIEnv *env, jobject thiz) {
    return createMatrix(env, getInstance<mce::Camera*>(env, thiz)->projectionMatrixStack.stack.top().m);
}

// ScreenContext Native Methods
jfloat ScreenContext_getPartialTicks(JNIEnv *env, jobject thiz) {
    return getInstance<ScreenContext*>(env, thiz)->partialTicks;
}

jobject ScreenContext_getCamera(JNIEnv *env, jobject thiz) {
    mce::Camera* camera = getInstance<ScreenContext*>(env, thiz)->camera;
    jclass cls = env->FindClass("helper/creeperbox/sdk/render/Camera");
    jobject obj = env->NewObject(cls, env->GetMethodID(cls,"<init>", "(J)V"),
                                 reinterpret_cast<long>(camera));
    return obj;
}

// UIRenderContext Native Methods
void UIRenderContext_renderItem(JNIEnv *env, jobject thiz, jobject item, jfloat x,
                                                jfloat y, jfloat scale, jfloat opacity, jfloat idk,
                                                jboolean enchantment) {
    ItemStack *i = getInstance<ItemStack *>(env, item);
    if (i->isValid()) {
        UIRenderContext *ctx = getInstance<UIRenderContext *>(env, thiz);
        BaseActorRenderContext baseActorRenderContext = BaseActorRenderContext(ctx->screenContext);
        if (baseActorRenderContext.renderer != nullptr) {
            baseActorRenderContext.renderer->renderGuiItemNew(&baseActorRenderContext, i, 0, x, y,
                                                              opacity, scale, idk,
                                                              enchantment);
        }
    }
}

jint UIRenderContext_renderItemNew(JNIEnv *env, jobject thiz, jbyteArray b,
                                                jobject player, jfloat x, jfloat y, jfloat scale,
                                                jfloat opacity, jfloat idk, jboolean enchantment) {
    int maxDamage = 0;
    BinaryStream* bin = new BinaryStream();
    convertByteArrayToString(env,b,bin->buffer);
    NetworkItemStackDescriptor descriptor = NetworkItemStackDescriptor();
    bin->readItem(&descriptor);
    ItemStack i = ItemStack();
    i.fromDescriptor(&descriptor, getInstance<LocalPlayer*>(env,player)->getLevel()->getBlockPalette(), false);
    if (i.isValid()) {
        i.pickUpTime = 0;
        UIRenderContext *ctx = getInstance<UIRenderContext *>(env, thiz);
        BaseActorRenderContext baseActorRenderContext = BaseActorRenderContext(ctx->screenContext);
        if (baseActorRenderContext.renderer != nullptr) {
            baseActorRenderContext.renderer->renderGuiItemNew(&baseActorRenderContext, &i, 0, x, y,
                                                              opacity, scale, idk,
                                                              enchantment);
            maxDamage = i.getItem()->getMaxDamage();
        }
    }
    delete bin;
    return maxDamage;
}

static const JNINativeMethod gScreenViewMethods[] = {
        {"a", "()V", (void*)ScreenView_exit},
        {"b", "()Ljava/lang/String;", (void*)ScreenView_getName}
};

static const JNINativeMethod gLevelRendererMethods[] = {
        {"a", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)LevelRenderer_getCameraPos},
        {"b", "()Lhelper/creeperbox/sdk/math/Vec3f;", (void*)LevelRenderer_getTargetCameraPos},
        {"c", "()V", (void*)LevelRenderer_resetArea}
};

static const JNINativeMethod gCameraMethods[] = {
        {"a", "()Lhelper/creeperbox/sdk/math/Matrix;", (void*)Camera_getViewMatrix},
        {"b", "()Lhelper/creeperbox/sdk/math/Matrix;", (void*)Camera_getModelMatrix},
        {"c", "()Lhelper/creeperbox/sdk/math/Matrix;", (void*)Camera_getProjectionMatrix}
};

static const JNINativeMethod gScreenContextMethods[] = {
        {"a", "()F", (void*)ScreenContext_getPartialTicks},
        {"b", "()Lhelper/creeperbox/sdk/render/Camera;", (void*)ScreenContext_getCamera}
};

static const JNINativeMethod gUIRenderContextMethods[] = {
        {"a", "(Lhelper/creeperbox/sdk/item/ItemStack;FFFFFZ)V", (void*)UIRenderContext_renderItem},
        {"b", "([BLhelper/creeperbox/sdk/entity/type/EntityLocalPlayer;FFFFFZ)I", (void*)UIRenderContext_renderItemNew}
};

void register_Render(JNIEnv* env) {
    jclass svClass = env->FindClass("helper/creeperbox/sdk/render/ScreenView");
    if(svClass) env->RegisterNatives(svClass, gScreenViewMethods, sizeof(gScreenViewMethods) / sizeof(gScreenViewMethods[0]));

    jclass lrClass = env->FindClass("helper/creeperbox/sdk/render/LevelRenderer");
    if(lrClass) env->RegisterNatives(lrClass, gLevelRendererMethods, sizeof(gLevelRendererMethods) / sizeof(gLevelRendererMethods[0]));

    jclass camClass = env->FindClass("helper/creeperbox/sdk/render/Camera");
    if(camClass) env->RegisterNatives(camClass, gCameraMethods, sizeof(gCameraMethods) / sizeof(gCameraMethods[0]));

    jclass scClass = env->FindClass("helper/creeperbox/sdk/render/ScreenContext");
    if(scClass) env->RegisterNatives(scClass, gScreenContextMethods, sizeof(gScreenContextMethods) / sizeof(gScreenContextMethods[0]));

    jclass uiClass = env->FindClass("helper/creeperbox/sdk/render/UIRenderContext");
    if(uiClass) env->RegisterNatives(uiClass, gUIRenderContextMethods, sizeof(gUIRenderContextMethods) / sizeof(gUIRenderContextMethods[0]));
}
