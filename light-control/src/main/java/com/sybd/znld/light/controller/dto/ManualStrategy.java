package com.sybd.znld.light.controller.dto;

import com.sybd.znld.model.lamp.dto.Message;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@ToString(callSuper = true)
public class ManualStrategy extends Command {
    public Action action;
    public Integer value;

    @Override
    public boolean isValid() {
        if(action == Action.CHANGE_BRIGHTNESS) {
            if(this.value <= 0 || this.value > 100) return false;
        }
        return super.isValid();
    }

    @Override
    public Message<Message.Bundle> toMessage() {
        var list = new ArrayList<Message.Bundle>();
        if(action == Action.OPEN) {
            list.add(new Message.Bundle(0, action.getValue()));
        } else if(action == Action.CLOSE) {
            list.add(new Message.Bundle(0, 0));
        } else {
            list.add(new Message.Bundle(this.value, 0));
        }
        return new Message<>(Message.Address.LAMP, Message.Model.MANUAL, list);
    }
}
