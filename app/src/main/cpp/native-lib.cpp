#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "zhuangsj"
#define LOG(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

extern "C"
jstring
Java_com_example_ndktest_ndk_NDKHelper_stringFromJNI(
        JNIEnv *env,
        jobject /* this */,
        jstring str) {
    jboolean copy;
    std::string hello = "This is ";
    char *strChar = (char *) env->GetStringUTFChars(str, &copy);
    hello.append(strChar);
    LOG("jstring= %s , getStringUTF= %s", str, strChar);
    return env->NewStringUTF(hello.c_str());
}
