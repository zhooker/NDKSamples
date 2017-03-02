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

extern "C"
jint
Java_com_example_ndktest_ndk_NDKHelper_sumArray
        (JNIEnv *env, jobject obj, jintArray j_array) {
    jint i, sum = 0;
    jint *c_array;
    jint arr_len;
    /**
     * 第一种是生成native层的数组的拷贝 GetIntArrayRegion
     */
    //1. 获取数组长度
    arr_len = env->GetArrayLength(j_array);
    //2. 根据数组长度和数组元素的数据类型申请存放java数组元素的缓冲区
    c_array = (jint *) malloc(sizeof(jint) * arr_len);
    //3. 初始化缓冲区
    memset(c_array, 0, sizeof(jint) * arr_len);
    printf("arr_len = %d ", arr_len);
    //4. 拷贝Java数组中的所有元素到缓冲区中
    env->GetIntArrayRegion(j_array, 0, arr_len, c_array);
    for (i = 0; i < arr_len; i++) {
        sum += c_array[i];  //5. 累加数组元素的和
    }
    free(c_array);  //6. 释放存储数组元素的缓冲区

    /**
     * 第二种是直接调用数组指针进行操作.GetIntArrayElements / SetIntArrayRegion / ReleaseIntArrayElements
     */
    // 可能数组中的元素在内存中是不连续的，JVM可能会复制所有原始数据到缓冲区，然后返回这个缓冲区的指针
    sum = 0;
    c_array = env->GetIntArrayElements(j_array,NULL);
    if (c_array == NULL) {
        return 0;   // JVM复制原始数据到缓冲区失败
    }
    arr_len = env->GetArrayLength(j_array);
    printf("arr_len = %d\n", arr_len);
    for (i = 0; i < arr_len; i++) {
        sum += c_array[i];
    }
    env->ReleaseIntArrayElements(j_array, c_array, 0); // 释放可能复制的缓冲区

    jint temp = 888;
    env->SetIntArrayRegion(j_array,0,1,&temp); //反射修改Java數組的值
    return sum;
}
