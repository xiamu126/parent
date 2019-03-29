package com.sybd.znld.util;

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
    public static boolean isBetween(int it, int min, int max){
        return it >= min && it <= max;
    }
    public static boolean isPositive(Integer it){
        return it != null && it > 0;
    }
    public static boolean isNegative(Integer it){
        return it != null && it < 0;
    }
    public static boolean isNegative(Short it){
        return it != null && it < 0;
    }
    public static boolean isZero(Integer it){
        return  it != null && it == 0;
    }
    public static boolean isPositiveOrZero(Integer it){
        return it != null && it >= 0;
    }
    public static boolean isNegativeOrZero(Integer it){
        return it != null && it <= 0;
    }

    public static boolean isInteger(Object obj){
        if(obj == null) return false;
        try {
            Integer.parseInt(obj.toString());
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}
