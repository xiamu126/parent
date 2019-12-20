package com.sybd.znld.model.lamp.dto;

import com.sybd.znld.model.MessageType;

public class LampAlarm {
    public MessageType type = MessageType.LAMP_ALARM;
    public LampAlarmOutput message;
}
