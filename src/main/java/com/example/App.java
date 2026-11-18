package com.example;
//first change
public class App {
    public static int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        System.out.println("Hello from Jenkins Maven build! 2 + 3 = " + add(2, 3));
        System.out.println("Auto-Trigger");
    }
}
