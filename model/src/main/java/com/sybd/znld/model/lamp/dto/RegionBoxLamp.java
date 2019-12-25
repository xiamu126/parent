package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RegionBoxLamp {
    public String id; // 区域id
    public String name; // 区域名称
    public List<Box> boxes = new ArrayList<>();

    public static class Box {
        public String id;
        public String imei; // 配电箱的imei
        public String name; // 配电箱的名称
        public Double lng; // 经度
        public Double lat; // 纬度
        public String status; // 配电箱的状态
        public List<Lamp> lamps = new ArrayList<>();

        public static class Lamp {
            public String id;
            public String imei; // 路灯的imei
            public String name; // 路灯的名称
            public Double lng; // 经度
            public Double lat; // 纬度
            public String status; // 路灯的状态
            @JsonProperty("is_fault")
            public Boolean isFault;
            @JsonProperty("is_light")
            public Boolean isLight;
            @JsonProperty("is_online")
            public Boolean isOnline;
            public Integer brightness;
            @JsonProperty("execution_mode")
            public String executionMode;
            @JsonProperty("strategy_name")
            public String strategyName; // 如果在策略模式中，这个就是正在执行的策略的名字
            public Long from;
            public Long to;
            public List<LampStrategy.Point> points;
            @JsonProperty("region_name")
            public String regionName;
            public List<String> modules = new ArrayList<>(); // 路灯安装的模块；
        }
    }
}
