package com.sybd.znld.light.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IValidForDbInsert;
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
public abstract class BaseStrategy extends Command {
    public String name; // 策略的名称
    public String id;
    @JsonProperty("user_id")
    public String userId; // 是谁新建这个策略的
    @JsonProperty("organ_id")
    public String organId;
    public String status; // 策略状态，新建策略的时候不需要，只在查询策略的时候会返回

    public Long from; // 时间统一以时间戳，这个时间戳里包含日和时
    public Long to;

    @JsonIgnore
    public Boolean isImmediate() {
        var from = this.getFromDate();
        if(from == null) return null;
        return from.isBefore(LocalDate.now()); // 如果开始时间为过去时间则认为这是一条即时命令
    }

    @JsonIgnore
    public LocalDate getFromDate() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.from);
            return dateTime.toLocalDate();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalTime getFromTime() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.from);
            return dateTime.toLocalTime();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalDate getToDate() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.to);
            return dateTime.toLocalDate();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalTime getToTime() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.to);
            return dateTime.toLocalTime();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalDateTime getFrom() {
        try {
            return MyDateTime.toLocalDateTime(this.from);
        } catch (Exception ignored) {}
        return null;
    }

    @JsonIgnore
    public LocalDateTime getTo() {
        try {
            return MyDateTime.toLocalDateTime(this.to);
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public boolean isValid() {
        if (!MyString.isUuid(userId)) {
            log.debug("非法的用户id[" + this.userId + "]");
            return false;
        }
        if (!MyString.isUuid(this.organId)) {
            log.debug("非法的组织id[" + this.organId + "]");
            return false;
        }
        if (MyString.isEmptyOrNull(name)) {// 策略名称可以重复
            log.debug("策略名字非法");
            return false;
        }
        var from = this.getFrom();
        var to = this.getTo();
        if (to.isBefore(LocalDateTime.now())) {
            log.debug("指定的截止日期[" + MyDateTime.toString(to, MyDateTime.FORMAT4) + "]为过去日期");
            return false;
        }
        if (to.isBefore(from)) {
            log.debug("指定的截止日期[" + MyDateTime.toString(to, MyDateTime.FORMAT1) + "]在开始时间[" + MyDateTime.toString(from, MyDateTime.FORMAT1) + "]后面");
            return false;
        }
        return super.isValid();
    }
}
