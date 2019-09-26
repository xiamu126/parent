package com.sybd.znld.ministar.model;

import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class SubtitleForRegion implements Serializable {
    public String title;
    public String userId;
    public String regionId;
    public Integer type;
    public List<Rgb> colors;
    public Integer speed;
    public Integer brightness;
    public Long beginTimestamp;
    public Long endTimestamp;

    public boolean isValid(){
        if(MyString.isEmptyOrNull(title)) return false;
        else if(!MyString.isUuid(userId)) return false;
        else if(!MyString.isUuid(regionId)) return false;
        else if(!Type.isValid(type)) return false;
        else if(!MyNumber.isPositive(speed)) return false;
        else if(brightness < 0 || brightness > 100) return false;
        else if(!MyDateTime.isAllFutureAndStrict(beginTimestamp, endTimestamp)) return false;
        else {
            for(var c : colors){
                if(!c.isValid()) return false;
            }
        }
        return true;
    }

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.beginTimestamp / 1000); // 毫秒时间戳转换为秒时间戳
        builder.append(",");
        builder.append(this.endTimestamp / 1000); // 毫秒时间戳转换为秒时间戳
        builder.append(",");
        builder.append(this.type); // 硬件设定 1=呼吸灯 2=闪烁灯 3=跑马灯
        builder.append(",");
        builder.append(this.speed);
        builder.append(",");
        builder.append(this.colors.size());
        builder.append(",");
        StringBuilder colors = new StringBuilder();
        for(var c : this.colors){
            colors.append(c.toString());
        }
        builder.append(colors.toString());
        return builder.toString();
    }
}
