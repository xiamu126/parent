package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;

public class LampOnOffLine {
    public MessageType type = MessageType.LAMP_ON_OFFLINE;
    public Message message;
    public static class Message {
        @JsonProperty("is_online")
        public Boolean isOnline;
        @JsonProperty("lamp_id")
        public String lampId;
        public String imei;
    }
}
