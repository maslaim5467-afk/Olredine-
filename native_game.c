#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_mycgame_MainActivity_getGameStatus(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello from C Native Code inside APK!");
}
