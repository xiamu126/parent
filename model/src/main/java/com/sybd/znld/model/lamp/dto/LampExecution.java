package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;

import java.util.List;

public class LampExecution {
    public MessageType type = MessageType.LAMP_EXECUTION;
    public Message message;
    public static class Message {
        public String lampId;
        @JsonProperty("is_fault")
        public Boolean isFault;
        @JsonProperty("is_light")
        public Boolean isLight;
        @JsonProperty("is_online")
        public Boolean isOnline;
        public Integer brightness;
        @JsonProperty("execution_mode")
        public String executionMode;
        @JsonProperty("strategy_name")
        public String strategyName; // 如果在策略模式中，这个就是正在执行的策略的名字
        public Long from;
        public Long to;
        public List<LampStrategy.Point> points;
        @JsonProperty("region_name")
        public String regionName;
    }
}
