package com.sybd.znld.ministar.model;

import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Subtitle implements Serializable {
    public String title;
    public String userId;
    //public String regionId;
    public Integer type;
    public List<Rgb> colors;
    public Integer speed;
    public Integer brightness;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        /*builder.append(this.beginTimestamp / 1000); // 毫秒时间戳转换为秒时间戳
        builder.append(",");
        builder.append(this.endTimestamp / 1000); // 毫秒时间戳转换为秒时间戳
        builder.append(",");*/
        builder.append(this.type); // 硬件设定 1=呼吸灯 2=闪烁灯 3=跑马灯
        builder.append(",");
        builder.append(this.speed);
        builder.append(",");
        builder.append(this.colors.size());
        builder.append(",");
        StringBuilder colors = new StringBuilder();
        for(var c : this.colors){
            var tmp =  new Rgb();
            tmp.r = c.r * brightness / 100;
            tmp.g = c.r * brightness / 100;
            tmp.b = c.b * brightness / 100;
            colors.append(tmp.toString());
        }
        builder.append(colors.toString());
        return builder.toString();
    }

    public boolean isValid(){
        if(MyString.isEmptyOrNull(title)) return false;
        else if(!MyString.isUuid(userId)) return false;
        //else if(!MyString.isUuid(regionId)) return false;
        else if(!Subtitle.Type.isValid(type)) return false;
        else if(!MyNumber.isPositive(speed)) return false;
        else if(brightness < 0 || brightness > 100) return false;
        else {
            for(var c : colors){
                if(!c.isValid()) return false;
            }
        }
        return true;
    }

    public String getTypeName(){
        switch (type){
            case Type.HX:
                return Type.HX_NAME;
            case Type.SS:
                 return Type.SS_NAME;
            case Type.PM:
                 return Type.PM_NAME;
            default:
                return null;
        }
    }
    public static Integer getTypeCode(String typeName){
        switch (typeName){
            case Type.HX_NAME: return Type.HX;
            case Type.SS_NAME: return Type.SS;
            case Type.PM_NAME: return Type.PM;
            default: return null;
        }
    }
    public String getColorsString(){
        StringBuilder builder = new StringBuilder();
        for(var c : colors){
            builder.append(c.toString());
        }
        return builder.toString();
    }

    public static class Type{
        public static final int HX = 1; // 呼吸灯
        public static final int SS = 2; // 闪烁灯
        public static final int PM = 3; // 跑马灯
        public static final String HX_NAME = "呼吸灯";
        public static final String SS_NAME = "闪烁灯";
        public static final String PM_NAME = "跑马灯";

        public static boolean isValid(int v){
            switch (v){
                case HX: case SS: case PM: return true;
                default: return false;
            }
        }

        public static boolean isValid(String v){
            switch (v){
                case HX_NAME: case SS_NAME: case PM_NAME: return true;
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

        public static Rgb getRgb(String color){
            if(MyString.isEmptyOrNull(color)) return null;
            var tmp = color.toCharArray();
            if(tmp.length != 6){
                return null;
            }
            var rgb = new Rgb();
            try{
                rgb.r = Integer.parseInt(color.substring(0,2), 16);
                rgb.g = Integer.parseInt(color.substring(1,3), 16);
                rgb.b = Integer.parseInt(color.substring(2), 16);
            }catch (Exception ex){
                return null;
            }
            return rgb;
        }

        public static boolean isValid(String color){
            var tmp = getRgbs(color);
            return tmp != null && !tmp.isEmpty();
        }

        public static List<Rgb> getRgbs(String color){
            if(MyString.isEmptyOrNull(color)) return null;
            var tmp = color.toCharArray();
            if(tmp.length % 6 != 0){
                return null;
            }
            var count = tmp.length / 6;
            var a = 0;
            var b = 6;
            var result = new ArrayList<Rgb>();
            for(var i = 0; i < count; i++){
                var rgb = getRgb(color.substring(a, b));
                if(rgb == null){
                    return null;
                }
                result.add(rgb);
                a = b;
                b = b + 6;
            }
            return result;
        }

        public static void main(String[] args) {
            var ret = getRgbs("F6F6F6F6F6F6F6F6F6");
            log.debug(ret.toString());
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
