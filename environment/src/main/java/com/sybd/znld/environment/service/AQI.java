package com.sybd.znld.environment.service;

import com.sybd.znld.environment.service.dto.AQIResult;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

@Slf4j
public class AQI {
    public static final List<Integer> IAQI = List.of(0, 50, 100, 150, 200, 300, 400, 500);
    public static final List<Integer> SO2_1_HOUR = List.of(0, 150, 500, 650, 800);
    public static final List<Integer> SO2_24_HOUR = List.of(0, 50, 150, 475, 800, 1600, 2100, 2620);
    public static final List<Integer> NO2_1_HOUR = List.of(0, 100, 200, 700, 1200, 2340, 3090, 3840);
    public static final List<Integer> NO2_24_HOUR = List.of(0, 40, 80, 180, 280, 565, 750, 940);
    public static final List<Integer> PM10_24_HOUR = List.of(0, 50, 150, 250, 350, 420, 500, 600);
    public static final List<Integer> CO_1_HOUR = List.of(0, 5, 10, 35, 60, 90, 120, 150);
    public static final List<Integer> CO_24_HOUR = List.of(0, 2, 4, 14, 24, 36, 48, 60);
    public static final List<Integer> O3_1_HOUR = List.of(0, 160, 200, 300, 400, 800, 1000, 1200);
    public static final List<Integer> O3_8_HOUR = List.of(0, 100, 160, 215, 265, 800);
    public static final List<Integer> PM25_24_HOUR = List.of(0, 35, 75, 115, 150, 250, 350, 500);
    public static final String SO2_NAME_VALUE = "SO2";
    public static final String NO2_NAME_VALUE = "NO2";
    public static final String CO_NAME_VALUE = "CO";
    public static final String O3_NAME_VALUE = "O3";
    public static final String PM10_NAME_VALUE = "PM10";
    public static final String PM25_NAME_VALUE = "PM2.5";

    public static AQIResult of1Hour(Double so2, Double no2, Double co, Double o3, Double pm10, Double pm25, Double o3_8, Double pm10_24, Double pm25_24) {
        var result = new AQIResult();
        var map = new HashMap<String, Double>();
        if(so2 != null){
            map.put("SO2", of1Hour(SO2_NAME_VALUE, so2));
        }
        if(no2 != null){
            map.put("NO2", of1Hour(NO2_NAME_VALUE, no2));
        }
        if(co != null){
            map.put("CO", of1Hour(CO_NAME_VALUE, co));
        }
        if(o3 != null){
            map.put("O3", of1Hour(O3_NAME_VALUE, o3));
        }
        if(pm10 != null){
            map.put("PM10", of1Hour(PM10_NAME_VALUE, pm10));
        }
        if(pm25 != null){
            map.put("PM2.5", of1Hour(PM25_NAME_VALUE, pm25));
        }
        if(o3_8 != null){
            map.put("O3_8", of8Hour(O3_NAME_VALUE, o3_8));
        }
        if(pm10_24 != null){
            map.put("PM10_24", of24Hour(PM10_NAME_VALUE, pm10_24));
        }
        if(pm25_24 != null){
            map.put("PM2.5_24", of24Hour(PM25_NAME_VALUE, pm25_24));
        }
        var items = new ArrayList<>(map.entrySet());
        items.sort(Comparator.comparingDouble(Map.Entry::getValue));
        var theMax = items.get(items.size() - 1); // 最后一个即最大值
        var theMaxValue = theMax.getValue();
        var nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        result.value = Float.parseFloat(nf.format(theMaxValue));
        result.primary = theMax.getKey().split("_")[0]; // 如果有后缀的话，去掉，如PM10_24，去掉_24
        if(theMaxValue >= 0 && theMaxValue <= 50){
            result.describe = "优";
        }else if(theMaxValue > 50 && theMaxValue <= 100){
            result.describe = "良";
        }else if(theMaxValue > 100 && theMaxValue <= 150){
            result.describe = "轻度污染";
        }else if(theMaxValue > 150 && theMaxValue <= 200){
            result.describe = "中度污染";
        }else if(theMaxValue > 200 && theMaxValue <= 300){
            result.describe = "重度污染";
        }else if(theMaxValue > 300){
            result.describe = "严重污染";
        }
        return result;
    }

    public static AQIResult of24Hour(Double so2, Double no2, Double co, Double o3_1, Double pm10, Double pm25, Double o3_8){
        var result = new AQIResult();
        var map = new HashMap<String, Double>();
        if(so2 != null){
            map.put("SO2", of24Hour(SO2_NAME_VALUE, so2));
        }
        if(no2 != null){
            map.put("NO2", of24Hour(NO2_NAME_VALUE, no2));
        }
        if(co != null){
            map.put("CO", of24Hour(CO_NAME_VALUE, co));
        }
        if(o3_1 != null){
            map.put("O3_1", of1Hour(O3_NAME_VALUE, o3_1));
        }
        if(pm10 != null){
            map.put("PM10", of24Hour(PM10_NAME_VALUE, pm10));
        }
        if(pm25 != null){
            map.put("PM25", of24Hour(PM25_NAME_VALUE, pm25));
        }
        if(o3_8 != null){
            map.put("O3_8", of8Hour(O3_NAME_VALUE, o3_8));
        }
        var items = new ArrayList<>(map.entrySet());
        items.sort(Comparator.comparingDouble(Map.Entry::getValue));
        var theMax = items.get(items.size() - 1); // 最后一个即最大值
        var theMaxValue = theMax.getValue();
        var nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        result.value = Float.parseFloat(nf.format(theMaxValue));
        result.primary = theMax.getKey().split("_")[0];
        if(theMaxValue >= 0 && theMaxValue <= 50){
            result.describe = "优";
        }else if(theMaxValue > 50 && theMaxValue <= 100){
            result.describe = "良";
        }else if(theMaxValue > 100 && theMaxValue <= 150){
            result.describe = "轻度污染";
        }else if(theMaxValue > 150 && theMaxValue <= 200){
            result.describe = "中度污染";
        }else if(theMaxValue > 200 && theMaxValue <= 300){
            result.describe = "重度污染";
        }else if(theMaxValue > 300){
            result.describe = "严重污染";
        }
        return result;
    }

    private static double of1Hour(String type, double value){
        var result = -1.0;
        var minValue = 0;
        var maxValue = 0;
        var index = 0;
        var minIAQI = 0;
        var maxIAQI = 0;
        switch (type){
            case SO2_NAME_VALUE:
                for(var i = 0 ; i < SO2_1_HOUR.size(); i++){
                    if(value <= SO2_1_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = SO2_1_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 如果index小于等于0，意味着第一个就是最高值，或者超过了列表中的全部值
                    minValue = SO2_1_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
            case NO2_NAME_VALUE:
                for(var i = 0 ; i < NO2_1_HOUR.size(); i++){
                    if(value <= NO2_1_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = NO2_1_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = NO2_1_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
            case CO_NAME_VALUE:
                for(var i = 0 ; i < CO_1_HOUR.size(); i++){
                    if(value <= CO_1_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = CO_1_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = CO_1_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
            case O3_NAME_VALUE:
                for(var i = 0 ; i < O3_1_HOUR.size(); i++){
                    if(value <= O3_1_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = O3_1_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = O3_1_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
        }
        return result;
    }
    private static double of8Hour(String type, double value){
        var result = -1.0;
        var minValue = 0;
        var maxValue = 0;
        var index = 0;
        var minIAQI = 0;
        var maxIAQI = 0;
        if (O3_NAME_VALUE.equals(type)) {
            for (var i = 0; i < O3_8_HOUR.size(); i++) {
                if (value <= O3_8_HOUR.get(i)) { // 找到最高值
                    index = i; // 最高位索引
                    maxValue = O3_8_HOUR.get(i);
                    break;
                }
            }
            if (index > 0) { // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                minValue = O3_8_HOUR.get(index - 1);
                minIAQI = IAQI.get(index - 1);
                maxIAQI = IAQI.get(index);
                result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
            }
        }
        return result;
    }
    private static double of24Hour(String type, double value){
        var result = -1.0;
        var minValue = 0;
        var maxValue = 0;
        var index = 0;
        var minIAQI = 0;
        var maxIAQI = 0;
        switch (type){
            case SO2_NAME_VALUE:
                for(var i = 0 ; i < SO2_24_HOUR.size(); i++){
                    if(value <= SO2_24_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = SO2_24_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = SO2_24_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
            case NO2_NAME_VALUE:
                for(var i = 0 ; i < NO2_24_HOUR.size(); i++){
                    if(value <= NO2_24_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = NO2_24_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = NO2_24_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
            case CO_NAME_VALUE:
                for(var i = 0 ; i < CO_24_HOUR.size(); i++){
                    if(value <= CO_24_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = CO_24_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = CO_24_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
            case PM10_NAME_VALUE:
                for(var i = 0 ; i < PM10_24_HOUR.size(); i++){
                    if(value <= PM10_24_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = PM10_24_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = PM10_24_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
            case PM25_NAME_VALUE:
                for(var i = 0 ; i < PM25_24_HOUR.size(); i++){
                    if(value <= PM25_24_HOUR.get(i)){ // 找到最高值
                        index = i; // 最高位索引
                        maxValue = PM25_24_HOUR.get(i);
                        break;
                    }
                }
                if(index > 0){ // 否则的话，意味着第一个就是最高值，或者超过了列表中的所有值，则不处理，返回默认值
                    minValue = PM25_24_HOUR.get(index - 1);
                    minIAQI = IAQI.get(index - 1);
                    maxIAQI = IAQI.get(index);
                    result = (maxIAQI - minIAQI) * 1.0 / (maxValue - minValue) * (value - minValue) + minIAQI;
                }
                break;
        }
        return result;
    }
}
