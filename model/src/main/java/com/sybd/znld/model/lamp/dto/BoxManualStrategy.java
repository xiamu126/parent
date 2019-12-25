package com.sybd.znld.model.lamp.dto;

import java.util.ArrayList;

public class BoxManualStrategy extends Command {
    public Message.CommonAction action;
    @Override
    public Message<Message.Bundle> toMessage() {
        var list = new ArrayList<Message.Bundle>();
        if(action == Message.CommonAction.OPEN) {
            list.add(new Message.CommonManualBundle(Message.CommonAction.OPEN));
        } else {
            list.add(new Message.CommonManualBundle(Message.CommonAction.CLOSE));
        }
        return new Message<>(Message.Address.BOX, Message.Model.MANUAL, list);
    }
}
