package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;

public class LampStatistic {
    public MessageType type = MessageType.LAMP_STATISTIC;
    public Message message;
    public static class Message {
        public String id;
        public Double voltage;
        public Double electricity;
        public Double power;
        @JsonProperty("power_factor")
        public Double powerFactor;
        public Double energy;
        public Double rate;
        public Double brightness;
        @JsonProperty("update_time")
        public Long updateTime;
    }
}
