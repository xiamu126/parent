package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;

public class LampStatistic {
    public MessageType type = MessageType.LAMP_STATISTIC;
    public Message message;
    public static class Message {
        public String id; // 路灯编号
        public Double voltage; // 电压
        public Double electricity; // 电流
        public Double power; // 功率
        @JsonProperty("power_factor")
        public Double powerFactor; // 功率因数
        public Double energy; // 电能
        public Double rate; // 频率
        public Integer brightness; // 亮度
        @JsonProperty("update_time")
        public Long updateTime;
    }
}
