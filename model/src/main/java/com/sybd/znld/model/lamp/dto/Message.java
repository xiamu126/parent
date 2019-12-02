package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sybd.znld.model.IEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 硬件接受的指令格式
public class Message {
    public List<Pair> s = new ArrayList<>();
    public Integer m; // 代表模式，0是手动，1是策略
    public Integer n; // 表示策略数目，就是s的长度

    public Message(Model model, List<Pair> list){
        this.m = model.getValue();
        this.s = list;
        this.n = list.size();
    }

    @AllArgsConstructor @NoArgsConstructor
    public static class Pair {
        public float v;
        public float t;
    }

    public enum Model implements IEnum {
        MANUAL(0), STRATEGY(1);
        Model(int v){
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }
}
