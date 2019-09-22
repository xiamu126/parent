package com.sybd.znld.ministar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

public class Subtitle {
    public LocalDateTime beginTime;
    public LocalDateTime endTime;
    public String regionId;
    public Integer type;
    public List<Rgb> colors;
    public Integer speed;
    public Integer brightness;
    public String creatorId;
    public LocalDateTime triggerTime = LocalDateTime.now();

    public static class Type{
        public static final int HX = 1; // 呼吸灯
        public static final int SS = 2; // 闪烁灯
        public static final int PM = 3; // 跑马灯

        public static boolean isValid(int v){
            switch (v){
                case HX: case SS: case PM: return true;
                default: return false;
            }
        }
    }

    @NoArgsConstructor
    public static class Rgb{
        public int r;
        public int g;
        public int b;

        public Rgb(int r, int g, int b){
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @BsonIgnore
        public boolean isValid(){
            return r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255;
        }

        @Override
        public String toString() {
            return String.format("%02X", r) + String.format("%02X", g) + String.format("%02X", b);
        }
    }
}
