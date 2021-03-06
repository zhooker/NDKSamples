package com.example.ndktest.ndk;

import android.os.Handler;

import com.example.ndktest.ClassField;

/**
 * Created by zhuangsj on 17-2-28.
 */

public class NDKHelper {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI(String info);

    public static native int sumArray(int[] arr);

    public static native String callJavaStaticMethod();

    public static native String callJavaInstaceMethod();

    public native static void accessInstanceField(ClassField obj);

    public native static int accessStaticField();

    public native static String callSuperInstanceMethod();

    public native static int openSharedLibrary(String path);

    public native static void callHandlerFromJava(Handler handler);
}
