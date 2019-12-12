package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class LampStatistic {
    public MessageType type = MessageType.LAMP_STATISTIC;
    public Message message;
    public static class Message {
        public String id; // 路灯编号
        public ValueError<Double> voltage; // 电压
        public ValueError<Double> electricity; // 电流
        public ValueError<Double> power; // 功率
        @JsonProperty("power_factor")
        public ValueError<Double> powerFactor; // 功率因数
        public ValueError<Double> energy; // 电能
        public ValueError<Double> rate; // 频率
        public ValueError<Integer> brightness; // 亮度
        @JsonProperty("update_time")
        public Long updateTime;

        @NoArgsConstructor @AllArgsConstructor
        public static class ValueError <T> {
            public T value;
            @JsonProperty("is_error")
            public Boolean isError;
        }
    }
}
