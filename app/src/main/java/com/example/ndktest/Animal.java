package com.example.ndktest;

/**
 * Created by zhooker on 2017/3/6.
 */

public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
        System.out.println("Animal Construct call...");
    }

    public String getName() {
        System.out.println("Animal.getName Call...");
        return this.name;
    }

    public void run() {
        System.out.println("Animal.run...");
    }
}
