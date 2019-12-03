package com.sybd.znld.light.controller.dto;

import com.sybd.znld.model.lamp.IStrategyMessage;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

@Slf4j
@ToString(callSuper = true)
public class BoxStrategy extends BaseStrategy {
    @Override
    public Message toMessage() {
        ZoneId zone = null;
        try {
            zone = ZoneId.of(zoneId);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            zone = ZoneId.systemDefault();
        }
        var list = new ArrayList<Message.Pair>();
        var fromTime = this.getFromTime();
        var toTime = this.getToTime();
        if (fromTime == null || toTime == null) return null;
        var seconds = Duration.between(LocalTime.of(0, 0, 0), fromTime).getSeconds();
        list.add(new Message.Pair(Action.OPEN.getValue(), seconds)); // 这个时间点打开配电箱
        seconds = Duration.between(LocalTime.of(0, 0, 0), toTime).getSeconds();
        list.add(new Message.Pair(Action.CLOSE.getValue(), seconds)); // 这个时间点关闭配电箱
        return new Message(Message.Model.STRATEGY, list);
    }
}
