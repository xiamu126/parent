package com.sybd.znld.light.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sybd.znld.model.IValidForDbInsertWithZoneId;
import com.sybd.znld.model.lamp.IStrategyMessage;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.*;

@Slf4j
@ToString
public abstract class BaseStrategy extends Command implements IValidForDbInsertWithZoneId {
    public String name; // 策略的名称
    public Long from; // 时间统一以时间戳，这个时间戳里包含日和时
    public Long to;

    @JsonIgnore
    public String zoneId = ZoneId.systemDefault().getId(); // aop会将配置文件中定义的时区覆盖这个默认值

    @JsonIgnore
    public LocalDate getFromDate() {
        try {
            var zone = ZoneId.of(zoneId);
            var dateTime = MyDateTime.toLocalDateTime(this.from, zone);
            return dateTime.toLocalDate();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalTime getFromTime() {
        try {
            var zone = ZoneId.of(zoneId);
            var dateTime = MyDateTime.toLocalDateTime(this.from, zone);
            return dateTime.toLocalTime();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalDate getToDate() {
        try {
            var zone = ZoneId.of(zoneId);
            var dateTime = MyDateTime.toLocalDateTime(this.to, zone);
            return dateTime.toLocalDate();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalTime getToTime() {
        try {
            var zone = ZoneId.of(zoneId);
            var dateTime = MyDateTime.toLocalDateTime(this.to, zone);
            return dateTime.toLocalTime();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalDateTime getFrom(String zoneId) {
        ZoneId zone = null;
        try {
            zone = ZoneId.of(zoneId);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            zone = ZoneId.systemDefault();
        }
        return MyDateTime.toLocalDateTime(this.from, zone);
    }

    @JsonIgnore
    public LocalDateTime getTo(String zoneId) {
        ZoneId zone = null;
        try {
            zone = ZoneId.of(zoneId);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            zone = ZoneId.systemDefault();
        }
        return MyDateTime.toLocalDateTime(this.to, zone);
    }

    @Override
    public boolean isValidForInsert(String zoneId) {
        try {
            if (MyString.isEmptyOrNull(name)) {// 策略名称可以重复
                log.debug("策略名字非法");
                return false;
            }
            var from = this.getFrom(zoneId);
            var to = this.getTo(zoneId);
            if (to.isBefore(LocalDateTime.now())) {
                log.debug("指定的截止日期[" + MyDateTime.toString(to, MyDateTime.FORMAT4) + "]为过去日期");
                return false;
            }
            if (to.isBefore(from)) {
                log.debug("指定的截止日期[" + MyDateTime.toString(to, MyDateTime.FORMAT1) + "]在开始时间[" + MyDateTime.toString(from, MyDateTime.FORMAT1) + "]后面");
                return false;
            }
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            return false;
        }
        return super.isValidForInsert();
    }
}
