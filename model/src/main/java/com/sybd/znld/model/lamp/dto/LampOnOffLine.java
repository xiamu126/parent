package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;

public class LampOnOffLine {
    public MessageType type = MessageType.LAMP_ON_OFFLINE;
    @JsonProperty("is_online")
    public Boolean isOnline;
}
