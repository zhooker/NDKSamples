package com.example.ndktest;

/**
 * Created by zhooker on 2017/3/6.
 */

public class Cat extends Animal {

    public Cat(String name) {
        super(name);
        System.out.println("Cat Construct call....");
    }

    @Override
    public String getName() {
        return "My name is " + this.name;
    }

    @Override
    public void run() {
        System.out.println(name + " Cat.run...");
    }
}