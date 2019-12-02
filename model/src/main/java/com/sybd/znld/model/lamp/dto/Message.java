package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 硬件接受的指令格式
public class Message {
    public List<Pair> s = new ArrayList<>();
    public Integer m; // 代表模式，0是手动，1是策略
    public Integer n; // 表示策略数目，就是s的长度

    @JsonIgnore
    public static final int MANUAL_MODEL_CODE = 0;
    @JsonIgnore
    public static final int STRATEGY_MODEL_CODE = 1;

    @AllArgsConstructor @NoArgsConstructor
    public static class Pair {
        public float v;
        public float t;
    }
}
