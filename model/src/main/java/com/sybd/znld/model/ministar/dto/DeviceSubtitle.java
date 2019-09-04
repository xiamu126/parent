package com.sybd.znld.model.ministar.dto;

import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeviceSubtitle implements Serializable {
    /*public String title;
    public String userId;
    public Integer deviceId;
    public Short action; // 0为停止，1为开始，2为存储指令
    public Effect effect; // 具体的效果
    public Short speed; // 效果速度
    public Long beginTimestamp;
    public Long endTimestamp;

    public static class Effect{
        public Short type; // 0为呼吸灯，1为跑马灯，2为全彩
        public List<Rgb> colors = new ArrayList<>(); // 颜色集合

        public boolean isValid(){
            if(!Type.isValid(type)) return false;
            if(colors.isEmpty()) return false;
            for(var c: colors){
                if(!c.isValid()) return false;
            }
            return true;
        }

        public static class Rgb{
            public Short r;
            public Short g;
            public Short b;
            public Rgb(){}
            public Rgb(short r, short g, short b){
                this.r = r;
                this.g = g;
                this.b = b;
            }
            public boolean isValid(){
                return r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255;
            }

            @Override
            public String toString() {
                return Integer.toHexString(r).toUpperCase() + Integer.toHexString(g).toUpperCase() + Integer.toHexString(b).toUpperCase();
            }
        }

        public static class Type{
            public static final short HX = 1; // 呼吸灯
            public static final short PM = 2; // 跑马灯
            public static final short QC = 3; // 全彩灯

            public static boolean isValid(short v){
                switch (v){
                    case HX: case PM: case QC: return true;
                    default: return false;
                }
            }
        }
    }

    public static class Action{
        public static final short STOP = 0;
        public static final short START = 1;
        public static final short SAVE = 2;

        public static boolean isValid(short v){
            switch (v){
                case STOP: case START: case SAVE: return true;
                default: return false;
            }
        }
    }

    public boolean isValid(){
        if(MyString.isEmptyOrNull(title)) return false;
        if(!MyString.isUuid(userId)) return false;
        if(!MyNumber.isPositive(deviceId)) return false;
        if(!Subtitle.Action.isValid(action)) return false;
        if(!effect.isValid()) return false;
        if(!MyNumber.isPositive(speed)) return false;
        if(beginTimestamp == null || MyDateTime.isPast(beginTimestamp, ZoneOffset.UTC)) return false;
        return endTimestamp != null && !MyDateTime.isBeforeOrEqual(endTimestamp, beginTimestamp, ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.beginTimestamp);
        builder.append(",");
        builder.append(this.endTimestamp);
        builder.append(",");
        builder.append(this.effect.type); // 硬件设定 1=呼吸灯 2=闪烁灯 3=跑马灯
        builder.append(",");
        builder.append(this.speed);
        builder.append(",");
        builder.append(this.effect.colors.size());
        builder.append(",");
        StringBuilder colors = new StringBuilder();
        for(var c : this.effect.colors){
            colors.append(c.toString());
        }
        builder.append(colors.toString());
        return builder.toString();
    }*/
}
