package com.sybd.znld.util;

import java.util.Random;

public class MyNumber {
    public static String toString(Double v){
        return Double.toString(v);
    }
    public static int rand(int min, int max){
        var random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    public static double rand(double min, double max){
        var random = new Random();
        return min + (max - min) * random.nextDouble();
    }
    public static Double getDouble(String num){
        try{
            return Double.parseDouble(num);
        }catch (Exception ignored){}
        return null;
    }
    public static boolean isBetween(int it, int min, int max){
        return it >= min && it <= max;
    }
    public static boolean isPositive(Integer it){
        return it != null && it > 0;
    }
    public static boolean isPositive(Short it){
        return it != null && it > 0;
    }
    public static boolean isNegative(Integer it){
        return it != null && it < 0;
    }
    public static boolean isNegative(Short it){
        return it != null && it < 0;
    }
    public static boolean isAnyNegative(Integer ...its){
        for(var it: its){
            if(isNegative(it)) return true;
        }
        return false;
    }
    public static boolean isAnyNegative(Short ...its){
        for(var it: its){
            if(isNegative(it)) return true;
        }
        return false;
    }
    public static boolean isZero(Integer it){
        return  it != null && it == 0;
    }
    public static boolean isPositiveOrZero(Integer it){
        return it != null && it >= 0;
    }
    public static boolean isPositiveOrZero(Short it){
        return it != null && it >= 0;
    }
    public static boolean isNegativeOrZero(Integer it){
        return it != null && it <= 0;
    }
    public static boolean isNegativeOrZero(Short it){
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
    public static boolean isBetween(String s, int min, int max){
        if(s == null) return false;
        try {
            var ret = Integer.parseInt(s);
            return ret >= min && ret <= max;
        }catch (Exception ex){
            return false;
        }
    }
    public static boolean isPositive(String s){
        if(s == null) return false;
        try {
            var ret = Integer.parseInt(s);
            return ret > 0;
        }catch (Exception ex){
            return false;
        }
    }
}
