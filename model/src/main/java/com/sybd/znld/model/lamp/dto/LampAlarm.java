package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;

public class LampAlarm {
    public MessageType type = MessageType.LAMP_ALARM;
    public Message message;

    public static class Message {
        public String id;
        @JsonProperty("lamp_id")
        public String lampId;
        @JsonProperty("lamp_name")
        public String lampName;
        public String content;
        public Long at;
        public String status;
        public String type;
        @JsonProperty("region_name")
        public String regionName;
    }
}
