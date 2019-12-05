package com.sybd.znld.light.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.IValidForDbInsert;
import com.sybd.znld.model.lamp.IStrategyMessage;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class Command implements IValidForDbInsert, IStrategyMessage {
    public List<StrategyTarget> targets; // 这个target可以是区域的集合或者单个设备的集合，至于是照明灯的id还是配电箱的id，由继承类决定
    @JsonProperty("user_id")
    public String userId; // 是谁新建这个策略的
    @JsonProperty("organ_id")
    public String organId;

    @JsonIgnore
    @Override
    public boolean isValidForInsert() {
        if (this.targets == null || this.targets.isEmpty()) {
            log.debug("目标集合targets为空");
            return false;
        }
        for (var t : this.targets) {
            if (t == null || t.target == null || t.ids == null || t.ids.isEmpty() || t.ids.stream().anyMatch(i -> !MyString.isUuid(i))) {
                log.debug("目标集合targets包含空值，或ids集合中包含非uuid值");
                return false;
            }
        }
        if (!MyString.isUuid(userId)) {
            log.debug("非法的用户id[" + this.userId + "]");
            return false;
        }
        if (!MyString.isUuid(this.organId)) {
            log.debug("非法的组织id[" + this.organId + "]");
            return false;
        }
        return true;
    }

    public enum Action implements IEnum {
        OPEN(100), CLOSE(0);

        Action(int v) {
            this.value = v;
        }

        private int value;

        @Override
        public int getValue() {
            return this.value;
        }
    }
}
