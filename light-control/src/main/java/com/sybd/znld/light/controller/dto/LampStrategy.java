package com.sybd.znld.light.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IValid;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString(callSuper = true)
public class LampStrategy extends BaseStrategy {
    public List<Point> points;
    @JsonProperty("init_brightness")
    public Integer initBrightness = 100; // 这个亮度值是用来指定开灯时的初始亮度

    @JsonIgnore
    @Override
    public boolean isValid() {
        if(this.initBrightness != null) {
            if(this.initBrightness < 0 || this.initBrightness > 100) {
                log.debug("指定的初始亮度百分比[" + this.initBrightness + "]错误");
                return false;
            }
        }
        var theBeginTime = this.getFromTime();
        var theEndTime = this.getToTime();
        if (theBeginTime == null || theEndTime == null) return false;
        // 当关联的时间点集合为非空时，需要判断里面包含的内容是否合法
        if (this.points != null && !this.points.isEmpty()) {
            for (var p : this.points) {
                try {
                    var dateTime = MyDateTime.toLocalDateTime(p.time);
                    if(dateTime == null) return false;
                    var time = dateTime.toLocalTime();
                    if(time.isAfter(LocalTime.MIDNIGHT) && time.isAfter(theEndTime)) {
                        log.debug("时间点[" + time + "]不在指定的开始结束区间内[" + theBeginTime + "," + theEndTime + "]");
                        return false;
                    }
                    if(time.isBefore(LocalTime.MIDNIGHT) && time.isBefore(theBeginTime)) {
                        log.debug("时间点[" + time + "]不在指定的开始结束区间内[" + theBeginTime + "," + theEndTime + "]");
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
        return super.isValid();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        public Long time; // 特定的时间点
        public Integer brightness; // 特定的亮度
    }
}
