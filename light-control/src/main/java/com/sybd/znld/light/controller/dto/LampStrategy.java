package com.sybd.znld.light.controller.dto;

import com.sybd.znld.model.lamp.IStrategyMessage;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.util.MyDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString(callSuper = true)
public class LampStrategy extends BaseStrategy implements IStrategyMessage {
    public List<Point> points;
    public List<Section> sections;
    public Integer brightness; // 这个亮度值是用来指定开灯时的初始亮度

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
        list.add(new Message.Pair(this.brightness, seconds)); // 这个时间点以这个亮度点亮照明灯
        seconds = Duration.between(LocalTime.of(0, 0, 0), toTime).getSeconds();
        list.add(new Message.Pair(Command.LAMP_CLOSE_CODE, seconds)); // 这个时间点关闭照明灯
        // 以上添加了开关灯时间点，下面继续添加时间点亮度和时间区间亮度
        for (var p : this.points) {
            var time = MyDateTime.toLocalDateTime(p.time, zone).toLocalTime();
            seconds = Duration.between(LocalTime.of(0, 0, 0), time).getSeconds();
            list.add(new Message.Pair(p.brightness, seconds)); // 这个时间点照明灯的亮度为这个
        }
        for (var s : this.sections) {
            var time = MyDateTime.toLocalDateTime(s.from, zone).toLocalTime();
            seconds = Duration.between(LocalTime.of(0, 0, 0), time).getSeconds();
            list.add(new Message.Pair(s.brightness, seconds)); // 这个时间点照明灯的亮度为这个
        }
        return new Message(Message.Model.STRATEGY, list);
    }

    @Override
    public boolean isValidForInsert(String zoneId) {
        ZoneId zone = null;
        try {
            zone = ZoneId.of(zoneId);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            zone = ZoneId.systemDefault();
        }
        var theBeginTime = this.getFromTime();
        var theEndTime = this.getToTime();
        if (theBeginTime == null || theEndTime == null) return false;
        // 当关联的时间点集合为非空时，需要判断里面包含的内容是否合法
        if (this.points != null && !this.points.isEmpty()) {
            for (var p : this.points) {
                try {
                    var dateTime = MyDateTime.toLocalDateTime(p.time, zone);
                    var time = dateTime.toLocalTime();
                    if (time.isBefore(theBeginTime) || time.isAfter(theEndTime)) {
                        log.debug("时间点[" + p.time + "]不在指定的开始结束区间内[" + theBeginTime + "," + theEndTime + "]");
                        return false;
                    }
                } catch (Exception ex) {
                    log.debug("指定的时间点[" + p.time + "]无法转为LocalDateTime");
                    log.debug(ex.getMessage());
                    return false;
                }
                if (p.brightness == null || p.brightness < 0 || p.brightness > 100) {
                    log.debug("指定的亮度百分比[" + p.brightness + "]错误");
                    return false;
                }
            }
        }
        // 当关联的时间区间集合为非空时，需要判断里面包含的内容是否合法
        if (this.sections != null && !this.sections.isEmpty()) {
            for (var s : this.sections) {
                try {
                    var from = MyDateTime.toLocalDateTime(s.from, zone);
                    var to = MyDateTime.toLocalDateTime(s.to, zone);
                    var beginTime = from.toLocalTime();
                    var endTime = to.toLocalTime();
                    if (beginTime.isAfter(endTime)) {
                        log.debug("在处理时间区间的时候，发生错误，开始时间[" + beginTime + "]在结束时间[" + endTime + "]之后");
                        return false;
                    }
                    if (beginTime.isBefore(theBeginTime) || beginTime.isAfter(theEndTime)) {
                        log.debug("处理时间区间的时候，发生错误，开始时间[" + beginTime + "]在总开始时间[" + theBeginTime + "]之前或在总结束时间[" + theEndTime + "]之后");
                        return false;
                    }
                    if (endTime.isBefore(theBeginTime) || endTime.isAfter(theEndTime)) {
                        log.debug("处理时间区间的时候，发生错误，结束时间[" + endTime + "]在总开始时间[" + theBeginTime + "]之前或在总结束时间[" + theEndTime + "]之后");
                        return false;
                    }
                } catch (Exception ex) {
                    log.debug("在处理时间区间的时候发生异常");
                    return false;
                }
                if (s.brightness == null || s.brightness < 0 || s.brightness > 100) {
                    log.debug("指定的亮度百分比[" + s.brightness + "]错误");
                    return false;
                }
            }
        }
        return super.isValidForInsert(zoneId);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        public Long time; // 特定的时间点
        public Integer brightness; // 特定的亮度
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Section {
        public Long from;
        public Long to;
        public Integer brightness;
    }
}
