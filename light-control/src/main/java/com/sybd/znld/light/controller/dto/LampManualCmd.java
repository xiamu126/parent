package com.sybd.znld.light.controller.dto;

import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.IValid;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.util.MyString;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString(callSuper = true)
public class LampManualCmd implements IValid {
    public List<String> targets;
    public Message.LampManualAction action;
    public Integer value;

    @Override
    public boolean isValid() {
        if (this.targets == null || this.targets.isEmpty()) {
            log.debug("目标集合targets为空");
            return false;
        }
        if (this.targets.stream().anyMatch(i -> !MyString.isUuid(i))) {
            log.debug("目标集合targets包含空值或非uuid值");
            return false;
        }
        if(this.action == Message.LampManualAction.CHANGE_BRIGHTNESS) {
            return this.value > 0 && this.value <= 100;
        }
        return true;
    }

    public List<Message.Bundle> toBundleList() {
        var list = new ArrayList<Message.Bundle>();
        if(this.action == Message.LampManualAction.OPEN) {
            list.add(new Message.LampManualBundle(Message.LampManualAction.OPEN));
        } else if(this.action == Message.LampManualAction.CLOSE) {
            list.add(new Message.LampManualBundle(Message.LampManualAction.CLOSE));
        } else {
            if(this.value != null && this.value >= 0 && this.value <= 100) {
                list.add(new Message.LampManualBundle(this.value));
            } else {
                return null;
            }
        }
        return list;
    }
}
