#ifndef HOPECLIENT_JAVADATA_H
#define HOPECLIENT_JAVADATA_H

#include <jni.h>

class JavaData{

public:
    static JavaVM* javaVM;
    static jclass hookProxyClass;
    static jmethodID onLocalPlayerTickMethod;
    static jmethodID onAttackMethod;
    static jmethodID onActorTickMethod;
    static jmethodID onPacketSendMethod;
    static jmethodID onPacketReceiveMethod;
    static jmethodID onPreRender2DMethod;
    static jmethodID onPreRender3DMethod;
    static jmethodID onPostRender3DMethod;
    static jmethodID onKeyEventMethod;
    static jmethodID onDestroyBlockMethod;
    static jmethodID onPressMethod;
    static jmethodID onHurtBobMethod;
    static jmethodID onRenderItem3DMethod;
    static jmethodID onPostRender2DMethod;
    static jmethodID onPostMoveMethod;
    static jmethodID onClientTickMethod;
    static jmethodID onSetCameraMethod;
    static jmethodID onChatMethod;
    static jmethodID onEncryptMethod;
    static jmethodID onLoginMethod;

public:
    static void Init(JavaVM * vm,JNIEnv* env){
        javaVM = vm;

        // 获取局部引用并转成全局引用，防止被 GC 回收
        jclass localClass = env->FindClass("helper/creeperbox/hook/HookProxy");
        hookProxyClass = (jclass)env->NewGlobalRef(localClass);
        env->DeleteLocalRef(localClass);

        onLocalPlayerTickMethod = env->GetStaticMethodID(hookProxyClass,"a", "(J)V");
        onActorTickMethod = env->GetStaticMethodID(hookProxyClass,"e","(J)V");
        onAttackMethod = env->GetStaticMethodID(hookProxyClass,"f","(JJ)V");
        onPacketSendMethod = env->GetStaticMethodID(hookProxyClass,"g", "([B)[B");
        onPacketReceiveMethod = env->GetStaticMethodID(hookProxyClass,"i", "([B)[B");
        onPreRender2DMethod = env->GetStaticMethodID(hookProxyClass,"d", "(JJ)V");
        onPreRender3DMethod = env->GetStaticMethodID(hookProxyClass,"h", "(JJ)V");
        onPostRender3DMethod = env->GetStaticMethodID(hookProxyClass,"j", "()V");
        onKeyEventMethod = env->GetStaticMethodID(hookProxyClass,"b", "(II)V");
        onDestroyBlockMethod = env->GetStaticMethodID(hookProxyClass,"c", "(JJIIIF)F");
        onPressMethod = env->GetStaticMethodID(hookProxyClass,"k", "(IFFI)Z");
        onHurtBobMethod = env->GetStaticMethodID(hookProxyClass,"l", "([F)[F");
        onRenderItem3DMethod = env->GetStaticMethodID(hookProxyClass, "m", "(FJ[F)[F");
        onPostRender2DMethod = env->GetStaticMethodID(hookProxyClass, "n", "(JJ)V");
        onPostMoveMethod = env->GetStaticMethodID(hookProxyClass, "o", "(J)V");
        onClientTickMethod = env->GetStaticMethodID(hookProxyClass, "p", "()V");
        onSetCameraMethod = env->GetStaticMethodID(hookProxyClass,"q", "(FFF)[F");
        onChatMethod = env->GetStaticMethodID(hookProxyClass,"r","(Ljava/lang/String;)Z");
        onEncryptMethod = env->GetStaticMethodID(hookProxyClass,"s","(Ljava/lang/String;)Ljava/lang/String;");
        onLoginMethod = env->GetStaticMethodID(hookProxyClass,"t","(Ljava/lang/String;)Ljava/lang/String;");
    }
};


#endif //HOPECLIENT_JAVADATA_H
