package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.MyEnum;
import com.sybd.znld.model.lamp.dto.Message;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LampStrategyModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String code = UUID.randomUUID().toString().replace("-", ""); // 策略代码，多个版本共用一个代码
    public Integer version = 1; // 策略版本号
    public String name; // 策略名称
    public LocalDate fromDate; // 策略开始日期
    public LocalDate toDate; // 策略结束日期
    public LocalTime fromTime; // 策略开始时间
    public LocalTime toTime; // 策略结束日期
    public boolean autoGenerateTime; // 策略的开始结束时间是否为自动生成的，即是否为根据经纬度生成开关灯时间
    public String organId; // 这个策略属于哪个分平台
    public String userId; // 谁创建这个策略的
    public Integer initBrightness = 100; // 开灯时的初始亮度
    public LocalTime at1 = LocalTime.NOON; // 特定时间点
    public Integer brightness1 = EMPTY_BRIGHTNESS; // 特定亮度
    public LocalTime at2 = LocalTime.NOON; // 特定时间点
    public Integer brightness2 = EMPTY_BRIGHTNESS; // 特定亮度
    public LocalTime at3 = LocalTime.NOON; // 特定时间点
    public Integer brightness3 = EMPTY_BRIGHTNESS; // 特定亮度
    public LocalTime at4 = LocalTime.NOON; // 特定时间点
    public Integer brightness4 = EMPTY_BRIGHTNESS; // 特定亮度
    public LocalTime at5 = LocalTime.NOON; // 特定时间点
    public Integer brightness5 = EMPTY_BRIGHTNESS; // 特定亮度
    public Status status = Status.OK;

    public static final int EMPTY_BRIGHTNESS = -1;

    @MyEnum
    public enum Status implements IEnum {
        OK(0), DELETED(1)
        ;
        Status(int v) {
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }

    public List<Message.Bundle> toBundleList() {
        if (this.fromTime == null || this.toTime == null) {
            return null;
        }
        if(this.initBrightness == null || this.initBrightness < 0 || this.initBrightness > 100) {
            return null;
        }
        var list = new ArrayList<Message.Bundle>();
        var seconds = Duration.between(LocalTime.MIDNIGHT, this.fromTime).getSeconds();
        list.add(new Message.LampBundle(seconds, this.initBrightness)); // 这个时间点以这个亮度点亮照明灯
        seconds = Duration.between(LocalTime.MIDNIGHT, this.toTime).getSeconds();
        list.add(new Message.LampBundle(seconds, Message.LampManualAction.CLOSE)); // 这个时间点关闭照明灯
        // 以上添加了开关灯时间点，下面继续添加时间点亮度和时间区间亮度
        if(this.brightness1 != null && this.brightness1 >= 0 && this.brightness1 <= 100 && this.at1 != null) {
            seconds = Duration.between(LocalTime.MIDNIGHT, this.at1).getSeconds();
            list.add(new Message.LampBundle(seconds, this.brightness1)); // 这个时间点以这个亮度亮灯
        }
        if(this.brightness2 != null && this.brightness2 >= 0 && this.brightness2 <= 100 && this.at2 != null) {
            seconds = Duration.between(LocalTime.MIDNIGHT, this.at2).getSeconds();
            list.add(new Message.LampBundle(seconds, this.brightness2)); // 这个时间点以这个亮度亮灯
        }
        if(this.brightness3 != null && this.brightness3 >= 0 && this.brightness3 <= 100 && this.at3 != null) {
            seconds = Duration.between(LocalTime.MIDNIGHT, this.at3).getSeconds();
            list.add(new Message.LampBundle(seconds, this.brightness3)); // 这个时间点以这个亮度亮灯
        }
        if(this.brightness4 != null && this.brightness4 >= 0 && this.brightness4 <= 100 && this.at4 != null) {
            seconds = Duration.between(LocalTime.MIDNIGHT, this.at4).getSeconds();
            list.add(new Message.LampBundle(seconds, this.brightness4)); // 这个时间点以这个亮度亮灯
        }
        if(this.brightness5 != null && this.brightness5 >= 0 && this.brightness5 <= 100 && this.at5 != null) {
            seconds = Duration.between(LocalTime.MIDNIGHT, this.at5).getSeconds();
            list.add(new Message.LampBundle(seconds, this.brightness5)); // 这个时间点以这个亮度亮灯
        }
        return list;
    }
}
