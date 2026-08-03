#ifndef HOPECLIENT_JNIUTILS_H
#define HOPECLIENT_JNIUTILS_H

#include <jni.h>
#include <string>

jbyteArray convertStringToByteArray(JNIEnv* env, const std::string* str);

std::string jstringToString(JNIEnv *env, jstring jStr);

JNIEnv* getEnv();

void convertByteArrayToString(JNIEnv* env, jbyteArray byteArray, std::string* resultStr);

void detachThread();


#endif //HOPECLIENT_JNIUTILS_H
