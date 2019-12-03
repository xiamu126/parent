package com.sybd.znld.light.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class RegionBoxLamp {
    public String id; // 区域id
    public String name; // 区域名称
    public List<Box> boxes = new ArrayList<>();

    public static class Box {
        public String imei; // 配电箱的imei
        public String name; // 配电箱的名称
        public Double lng; // 经度
        public Double lat; // 纬度
        public String status; // 配电箱的状态
        public List<Lamp> lamps = new ArrayList<>();

        public static class Lamp {
            public String imei; // 路灯的imei
            public String name; // 路灯的名称
            public Double lng; // 经度
            public Double lat; // 纬度
            public String status; // 路灯的状态
            public List<String> modules = new ArrayList<>(); // 路灯安装的模块；
        }
    }
}
