package com.sybd.znld.light.controller.dto;

import com.sybd.znld.model.lamp.dto.Message;
import lombok.ToString;

import java.util.ArrayList;

@ToString
public class ManualBrightnessStrategy extends Command {
    public int brightness;

    @Override
    public boolean isValidForInsert() {
        if(brightness < 0 || brightness > 100) return false;
        return super.isValidForInsert();
    }

    @Override
    public Message toMessage() {
        var list = new ArrayList<Message.Pair>();
        list.add(new Message.Pair(brightness, 0)); // 立即调整到这个亮度
        return new Message(Message.Model.MANUAL, list);
    }
}
