#include "JavaData.h"

JavaVM * JavaData::javaVM = nullptr;
jclass JavaData::hookProxyClass = nullptr;
jmethodID JavaData::onLocalPlayerTickMethod = nullptr;
jmethodID JavaData::onActorTickMethod = nullptr;
jmethodID JavaData::onAttackMethod = nullptr;
jmethodID JavaData::onPacketSendMethod = nullptr;
jmethodID JavaData::onPacketReceiveMethod = nullptr;
jmethodID JavaData::onPreRender2DMethod = nullptr;
jmethodID JavaData::onPreRender3DMethod = nullptr;
jmethodID JavaData::onPostRender3DMethod = nullptr;
jmethodID JavaData::onKeyEventMethod = nullptr;
jmethodID JavaData::onDestroyBlockMethod = nullptr;
jmethodID JavaData::onPressMethod = nullptr;
jmethodID JavaData::onHurtBobMethod = nullptr;
jmethodID JavaData::onRenderItem3DMethod = nullptr;
jmethodID JavaData::onPostRender2DMethod = nullptr;
jmethodID JavaData::onPostMoveMethod = nullptr;
jmethodID JavaData::onClientTickMethod = nullptr;
jmethodID JavaData::onSetCameraMethod = nullptr;
jmethodID JavaData::onChatMethod = nullptr;
jmethodID JavaData::onEncryptMethod = nullptr;
jmethodID JavaData::onLoginMethod = nullptr;

