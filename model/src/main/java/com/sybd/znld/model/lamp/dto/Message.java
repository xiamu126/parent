package com.sybd.znld.model.lamp.dto;

import com.sybd.znld.model.IEnum;

import java.util.List;

// 硬件接受的指令格式
public class Message <T> {
    public Integer A;
    public List<T> S;
    public Integer M; // 代表模式，0是手动，1是策略

    public Message(Address address, Model model, List<T> list){
        this.A = address.getValue();
        this.M = model.getValue();
        this.S = list;
    }

    public static abstract class Bundle {
        public long t;
        public int v;
    }

    public static class LampBundle extends Bundle {

    }

    public static class BoxBundle extends Bundle {

    }

    public static class MiniStarBundle extends Bundle {
        public int m;
        public int p;
        public int c;
        public String r;
        public MiniStarBundle(MiniStarType type, int period, int colorCount, String colorRgb) {
            this.m = type.getValue();
            this.p = period;
            this.c = colorCount;
            this.r = colorRgb;
        }
    }

    public enum Model implements IEnum {
        MANUAL(0),  // 手动开关
        STRATEGY(1); // 策略开关
        Model(int v){
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }

    public enum Address implements IEnum {
        BOX(0), LAMP(1), SCREEN(2), FAN(3), ALARM(4), PAD(5), MINI_STAR(6);
        Address(int v){
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }

    public enum MiniStarType implements IEnum {
        BREATHING(1), FLICKERING(2), RUNNING(3);
        MiniStarType(int v){
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }
}
