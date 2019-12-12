package com.sybd.znld.light.controller.dto;

import com.sybd.znld.model.lamp.dto.Message;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

@Slf4j
@ToString(callSuper = true)
public class BoxStrategy extends BaseStrategy {
    @Override
    public Message<Message.Bundle> toMessage() {
        var list = new ArrayList<Message.Bundle>();
        var fromTime = this.getFromTime();
        var toTime = this.getToTime();
        if (fromTime == null || toTime == null) return null;
        var seconds = Duration.between(LocalTime.of(0, 0, 0), fromTime).getSeconds();
        list.add(new Message.Bundle(seconds, Action.OPEN.getValue())); // 这个时间点打开配电箱
        seconds = Duration.between(LocalTime.of(0, 0, 0), toTime).getSeconds();
        list.add(new Message.Bundle(seconds, Action.CLOSE.getValue())); // 这个时间点关闭配电箱
        return new Message<>(Message.Address.BOX, Message.Model.STRATEGY, list);
    }
}
