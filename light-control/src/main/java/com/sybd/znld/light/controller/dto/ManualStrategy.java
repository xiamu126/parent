package com.sybd.znld.light.controller.dto;

import com.sybd.znld.model.lamp.dto.Message;

import java.util.ArrayList;

public class ManualStrategy extends Command {
    public Action action;

    @Override
    public Message toMessage() {
        var list = new ArrayList<Message.Pair>();
        list.add(new Message.Pair(action.getValue(), 0)); // 立即调整到这个亮度
        return new Message(Message.Model.MANUAL, list);
    }
}
