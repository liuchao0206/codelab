//
// Created by 刘超 on 2017/8/23.
//
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_coderpage_libjni_NativeInterface_stringFromJni(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "String from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint  JNICALL
Java_com_coderpage_libjni_NativeInterface_add(
        JNIEnv *env,
        jobject /* this */,
        jint a,
        jint b) {
    int result = (jint) (a + b);
    return (jint) result;
}
