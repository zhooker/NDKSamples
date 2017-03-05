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

/*
 * CallStaticXXXMethod(env, clazz, methodID, ...)
 * CallStaticXXXMethodV(env, clazz, methodID, va_list args)
 * CallStaticXXXMethodA(env, clazz, methodID, const jvalue args)
 *
 * Method:    callJavaStaticMethod
 * Signature: ()V
 */
extern "C"
jstring
Java_com_example_ndktest_ndk_NDKHelper_callJavaStaticMethod
        (JNIEnv *env, jclass cls) {
    jclass clazz = NULL;
    jstring str_arg = NULL;
    jmethodID mid_static_method;
    // 1、从classpath路径下搜索ClassMethod这个类，并返回该类的Class对象
    clazz = env->FindClass("com/example/ndktest/ClassMethod");
    if (clazz == NULL) {
        LOG("找不到'com.example.ndktest.ClassMethod'这个类");
        return env->NewStringUTF("找不到'com.example.ndktest.ClassMethod'这个类");
    }

    // 2、从clazz类中查找callStaticMethod方法
    mid_static_method = env->GetStaticMethodID(clazz,"callStaticMethod","(Ljava/lang/String;I)Ljava/lang/String;");
    if (mid_static_method == NULL) {
        LOG("找不到callStaticMethod这个静态方法。");
        return env->NewStringUTF("找不到callStaticMethod这个静态方法。");
    }

    // 3、调用clazz类的callStaticMethod静态方法
    str_arg = env->NewStringUTF("我是静态方法");
    //env->CallStaticVoidMethod(clazz,mid_static_method, str_arg, 100);
    jstring result = (jstring)env->CallStaticObjectMethod(clazz,mid_static_method, str_arg, 100);

    // 删除局部引用
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(str_arg);

    return result;
}

/*
 * CallXXXMethod
 *
 * Method:    callJavaInstaceMethod
 * Signature: ()V
 */
extern "C"
jstring
Java_com_example_ndktest_ndk_NDKHelper_callJavaInstaceMethod
        (JNIEnv *env, jclass cls)
{
    jclass clazz = NULL;
    jobject jobj = NULL;
    jmethodID mid_construct = NULL;
    jmethodID mid_instance = NULL;
    jstring str_arg = NULL;
    // 1、从classpath路径下搜索ClassMethod这个类，并返回该类的Class对象
    clazz = env->FindClass("com/example/ndktest/ClassMethod");
    if (clazz == NULL) {
        LOG("找不到'com.example.ndktest.ClassMethod'这个类");
        return env->NewStringUTF("找不到'com.example.ndktest.ClassMethod'这个类");
    }

    // 2、获取类的默认构造方法ID
    mid_construct = env->GetMethodID(clazz, "<init>","()V");
    if (mid_construct == NULL) {
        LOG("找不到默认的构造方法");
        return env->NewStringUTF("找不到默认的构造方法");
    }

    // 3、查找实例方法的ID
    mid_instance = env->GetMethodID(clazz, "callInstanceMethod","(Ljava/lang/String;I)Ljava/lang/String;");
    if (mid_instance == NULL) {
        return env->NewStringUTF("找不到callInstanceMethod方法");
    }

    // 4、创建该类的实例
    jobj = env->NewObject(clazz,mid_construct);
    if (jobj == NULL) {
        LOG("创建com.example.ndktest.ClassMethod类失败。");
        return env->NewStringUTF("创建com.example.ndktest.ClassMethod类失败。");
    }

    // 5、调用对象的实例方法
    str_arg = env->NewStringUTF("我是实例方法");
    //env->CallVoidMethod(jobj,mid_instance,str_arg,200);
    jstring result = (jstring)env->CallObjectMethod(jobj,mid_instance, str_arg, 200);

    // 删除局部引用
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(jobj);
    env->DeleteLocalRef(str_arg);

    return result;
}

/*
 *
 * Method:    accessInstanceField
 * Signature: ()V
 */
extern "C"
void
Java_com_example_ndktest_ndk_NDKHelper_accessInstanceField
        (JNIEnv *env, jclass cls, jobject obj)
{
    jclass clazz;
    jfieldID fid;
    jstring j_str;
    jstring j_newStr;
    const char *c_str = NULL;

    // 1.获取ClassField类的Class引用
    clazz = env->GetObjectClass(obj);
    if (clazz == NULL) {
        return;
    }

    // 2. 获取ClassField类实例变量str的属性ID
    fid = env->GetFieldID(clazz,"str", "Ljava/lang/String;");
    if (clazz == NULL) {
        return;
    }

    // 3. 获取实例变量str的值
    j_str = (jstring)env->GetObjectField(obj,fid);

    // 4. 将unicode编码的java字符串转换成C风格字符串
    c_str = env->GetStringUTFChars(j_str,NULL);
    if (c_str == NULL) {
        return;
    }
    LOG("In C--->ClassField.str = %s\n", c_str);
    env->ReleaseStringUTFChars(j_str, c_str);

    // 5. 修改实例变量str的值
    j_newStr = env->NewStringUTF("This is C String");
    if (j_newStr == NULL) {
        return;
    }

    env->SetObjectField(obj, fid, j_newStr);

    // 6.删除局部引用
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(j_str);
    env->DeleteLocalRef(j_newStr);
}

/*
 *
 * Method:    accessStaticField
 * Signature: ()V
 */
extern "C"
jint
Java_com_example_ndktest_ndk_NDKHelper_accessStaticField
        (JNIEnv *env, jclass cls)
{
    jclass clazz;
    jfieldID fid;
    jint num;

    //1.获取ClassField类的Class引用
    clazz = env->FindClass("com/example/ndktest/ClassField");
    if (clazz == NULL) {    // 错误处理
        return -999;
    }

    //2.获取ClassField类静态变量num的属性ID
    fid = env->GetStaticFieldID(clazz, "num", "I");
    if (fid == NULL) {
        return -999;
    }

    // 3.获取静态变量num的值
    num = env->GetStaticIntField(clazz,fid);
    printf("In C--->ClassField.num = %d\n", num);

    // 4.修改静态变量num的值
    env->SetStaticIntField(clazz, fid, 80);

    // 删除属部引用
    env->DeleteLocalRef(clazz);

    return num;
}