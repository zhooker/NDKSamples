package com.example.ndktest;

import android.util.Log;

/**
 * ClassMethod.java
 * 用于本地代码调用
 */
public class ClassMethod {

    public static final String TAG = "zhuangsj";

    private static String callStaticMethod(String str, int i) {
        return "ClassMethod::callStaticMethod " + str + "," + i;
    }

    public  String callInstanceMethod(String str, int i) {
        return "ClassMethod::callInstanceMethod " + str + "," + i;
    }
}