package com.sybd.znld.service.ministar.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter @Setter
public class Subtitle implements Serializable {
    public Integer deviceId;
    public Short action; // 0为停止，1为开始，2为存储指令
    public Effect effect; // 具体的效果
    public Short speed; // 效果速度
    public Long beginTimestamp;
    public Long endTimestamp;
    public Short effectTotalCount;
    public Short effectCurrentIndex;

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

            public boolean isValid(){
                return r >= 0 && g >= 0 && b >= 0;
            }
        }

        public static class Type{
            public static final short HX = 0;
            public static final short PM = 1;
            public static final short QC = 2;

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
        if(deviceId <= 0) return false;
        if(!Action.isValid(action)) return false;
        if(!effect.isValid()) return false;
        if(speed <=0) return false;
        if(MyDateTime.isPast(beginTimestamp, ZoneOffset.UTC)) return false;
        if(MyDateTime.isBeforeOrEqual(endTimestamp, beginTimestamp, ZoneOffset.UTC)) return false;
        if(effectTotalCount <= 0) return false;
        return effectCurrentIndex < effectTotalCount;
    }

    @Override
    public String toString() {
        try{
            if(!this.isValid()) return "";
            var stringBuilder = new StringBuilder();
            stringBuilder.append("DG");
            var lamp_number = 0x3fff; // 14位，最大0x3fff
            var lamp_status = action;
            var number_status = (short)(lamp_number << 2 | lamp_status);
            var t1 = (byte)(number_status >>> 8);
            var t2 = (byte)(number_status & 0x00ff);
            stringBuilder.append(String.format("%02X", t1));
            stringBuilder.append(String.format("%02X", t2));
            var cmd_count = (byte)0;
            stringBuilder.append(String.format("%02X", cmd_count));
            var item_count = (byte)(effectTotalCount & 0x00ff);
            var current_item = (byte)(effectCurrentIndex & 0x00ff);
            stringBuilder.append(String.format("%02X", item_count));
            stringBuilder.append(String.format("%02X", current_item));
            var color_info = effect.type;
            var color_count = effect.colors.size();
            var color_info_count = (byte)(color_info << 5 | (color_count & 0x1f));
            stringBuilder.append(String.format("%02X", color_info_count));
            for(var i = 0; i < effect.colors.size(); i++){
                var color_index = (byte)i;
                var color_r = (byte)(effect.colors.get(i).r & 0x00ff);
                var color_g = (byte)(effect.colors.get(i).g & 0x00ff);
                var color_b = (byte)(effect.colors.get(i).b & 0x00ff);
                stringBuilder.append(String.format("%02X", color_r));
                stringBuilder.append(String.format("%02X", color_g));
                stringBuilder.append(String.format("%02X", color_b));
            }
            var speed = (byte)(this.speed & 0x00ff);
            stringBuilder.append(String.format("%02X", speed));
            var begin_time = (int)(this.beginTimestamp/1000); // +1取整
            var end_time = (int)(this.endTimestamp/1000);
            var b1 = (byte)(begin_time >>> 24);
            var b2 = (byte)((begin_time >>> 16) & 0x0000_00ff);
            var b3 = (byte)((begin_time >>> 8) & 0x0000_00ff);
            var b4 = (byte)(begin_time & 0x0000_00ff);
            stringBuilder.append(String.format("%02X", b1));
            stringBuilder.append(String.format("%02X", b2));
            stringBuilder.append(String.format("%02X", b3));
            stringBuilder.append(String.format("%02X", b4));
            b1 = (byte)(end_time >>> 24);
            b2 = (byte)((end_time >>> 16) & 0x0000_00ff);
            b3 = (byte)((end_time >>> 8) & 0x0000_00ff);
            b4 = (byte)(end_time & 0x0000_00ff);
            stringBuilder.append(String.format("%02X", b1));
            stringBuilder.append(String.format("%02X", b2));
            stringBuilder.append(String.format("%02X", b3));
            stringBuilder.append(String.format("%02X", b4));
            stringBuilder.append("JW");
            log.debug(stringBuilder.toString());
            return stringBuilder.toString();
        }catch (Exception ex){
            return "";
        }
    }

    public static void test(String s){
        var bytes = s.getBytes();
        var DG = "DG".getBytes();
        var index = 0;
        var head = Arrays.copyOfRange(bytes, index, index += 2); // 校验头
        var number_status = Arrays.copyOfRange(bytes, index, index += 2); // 路灯编号与当前状态
        var lamp_index = number_status[0] << 6 | number_status[1] >>> 2; // 前十四位为路灯编号
        var lamp_status = number_status[1] & 0x03; // 最后两位为状态
        var cmd_count = bytes[index]; // 当前指令数
        var item_count = bytes[index += 1]; // 总效果数
        var current_item = bytes[index += 1]; // 当前效果序号
        var effect_color = bytes[index += 1]; // 效果编码与颜色数
        var effect_number = effect_color >>> 5; // 效果编码
        var color_count = effect_color & 0x1f; // 颜色数
        var color_info = Arrays.copyOfRange(bytes, index += 1, index += (4 * color_count)); // 所有颜色信息
        var speed = bytes[index];
        var begin_time = Arrays.copyOfRange(bytes, index += 1, index += 4);
        var end_time = Arrays.copyOfRange(bytes, index, index += 4);
        var tail = bytes[index];
        System.out.println(new String(head, StandardCharsets.UTF_8));
        System.out.println(lamp_index);
        System.out.println(lamp_status);
        System.out.println(cmd_count);
        System.out.println(item_count);
        System.out.println(current_item);
        System.out.println(effect_number);
        System.out.println(color_count);
        /*if(!MyByte.equals(DG, Arrays.copyOfRange(bytes,0,2))){
            return false;
        }*/
    }
}