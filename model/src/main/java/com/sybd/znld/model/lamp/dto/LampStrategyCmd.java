package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IValid;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LampStrategyCmd implements IValid {
    public List<String> targets; // 路灯的id集合
    @JsonProperty("lamp_strategy_id")
    public String lampStrategyId;

    @Override
    public boolean isValid() {
        if(!MyString.isUuid(this.lampStrategyId)) return false;
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
