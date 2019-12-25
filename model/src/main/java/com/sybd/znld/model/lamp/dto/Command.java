package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.IValid;
import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.model.lamp.IStrategyMessage;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
public abstract class Command implements IValid, IStrategyMessage {
    public List<String> targets; // 设备id集合，至于是照明灯的id还是配电箱的id，由继承类决定

    @JsonIgnore
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
        return true;
    }
}
