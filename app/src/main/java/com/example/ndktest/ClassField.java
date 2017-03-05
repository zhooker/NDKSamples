package com.example.ndktest;

/**
 * Created by zhooker on 2017/3/5.
 */

public class ClassField {

    private static int num;

    private String str;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        ClassField.num = num;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}