package com.sybd.znld.light.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IValid;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.*;

@Slf4j
@ToString
public abstract class BaseStrategy implements IValid {
    public String name; // 策略的名称
    public String id;
    @JsonProperty("user_id")
    public String userId; // 是谁新建这个策略的
    @JsonProperty("organ_id")
    public String organId;
    @JsonProperty("from")
    public Long from; // 时间统一以时间戳，这个时间戳里包含日和时
    @JsonProperty("to")
    public Long to;

    @JsonIgnore
    public LocalDate getFromDate() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.from);
            if(dateTime == null) return null;
            return dateTime.toLocalDate();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalTime getFromTime() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.from);
            if(dateTime == null) return null;
            return dateTime.toLocalTime();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalDate getToDate() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.to);
            if(dateTime == null) return null;
            return dateTime.toLocalDate();
        } catch (Exception ignored) {
        }
        return null;
    }

    @JsonIgnore
    public LocalTime getToTime() {
        try {
            var dateTime = MyDateTime.toLocalDateTime(this.to);
            if(dateTime == null) return null;
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
        if(from == null || to == null) return false;
        if(from.getYear() == 1973) {
            // 开始年份为1973，意味着只关心月份，则不考虑过去时间的问题，但此时日必须为1
            if(to.getYear() != 1973) return false;
            if(from.getDayOfMonth() != 1 || to.getDayOfMonth() != 1) {
                return false;
            }
            var fromMonth = from.getMonthValue();
            var toMonth = to.getMonthValue();
            if(fromMonth > toMonth) {
                log.debug("指定的截止月份[" + toMonth + "]在开始月份[" + fromMonth + "]后面");
                return false;
            }
        } else {
            // 如果不是1973年，意味着必须是完整的年月日
            if(from.isBefore(LocalDateTime.now())) {
                log.debug("指定的开始日期[" + MyDateTime.toString(from, MyDateTime.FORMAT4) + "]为过去日期");
                return false;
            }
            if (to.isBefore(LocalDateTime.now())) {
                log.debug("指定的截止日期[" + MyDateTime.toString(to, MyDateTime.FORMAT4) + "]为过去日期");
                return false;
            }
            if (to.isBefore(from)) {
                log.debug("指定的截止日期[" + MyDateTime.toString(to, MyDateTime.FORMAT1) + "]在开始时间[" + MyDateTime.toString(from, MyDateTime.FORMAT1) + "]后面");
                return false;
            }
        }

        return true;
    }
}
