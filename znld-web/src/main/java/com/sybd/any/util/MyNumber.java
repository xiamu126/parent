package com.sybd.any.util;

import java.util.Random;

public class MyNumber {
    public static int rand(int min, int max){
        var random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    public static double rand(double min, double max){
        var random = new Random();
        return min + (max - min) * random.nextDouble();
    }
}
