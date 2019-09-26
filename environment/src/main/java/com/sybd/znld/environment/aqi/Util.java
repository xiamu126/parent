package com.sybd.znld.environment.aqi;

import java.util.List;

public class Util {
    public static List<Integer> IAQI = List.of(0, 50, 100, 150, 200, 300, 400, 500);
    public static List<Integer> SO2_1_HOUR = List.of(0, 150, 500, 650, 800);
    public static List<Integer> SO2_24_HOUR = List.of(0, 50, 150, 475, 800, 1600, 2100, 2620);
    public static List<Integer> NO2_1_HOUR = List.of(0, 100, 200, 700, 1200, 2340, 3090, 3840);
    public static List<Integer> NO2_24_HOUR = List.of(0, 40, 80, 180, 280, 565, 750, 940);
    public static List<Integer> PM10_24_HOUR = List.of(0, 50, 150, 250, 350, 420, 500, 600);
    public static List<Integer> CO_1_HOUR = List.of(0, 5, 10, 35, 60, 90, 120, 150);
    public static List<Integer> CO_24_HOUR = List.of(0, 2, 4, 14, 24, 36, 48, 60);
    public static List<Integer> O3_1_HOUR = List.of(0, 160, 200, 300, 400, 800, 1000, 1200);
    public static List<Integer> O3_8_HOUR = List.of(0, 100, 160, 215, 265, 800);
    public static List<Integer> PM25_24_HOUR = List.of(0, 35, 75, 115, 150, 250, 350, 500);
}
