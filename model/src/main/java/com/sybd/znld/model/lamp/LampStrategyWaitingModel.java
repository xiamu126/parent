package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.MyEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LampStrategyWaitingModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String lampId; // 照明灯id
    public String lampStrategyId; // 关联了哪个策略
    public String organId;
    public LocalDateTime triggerTime = LocalDateTime.now();
    public Status status = Status.UNUSED;

    @MyEnum
    public enum Status implements IEnum {
        UNUSED(0), USED(1)
        ;
        Status(int v) {
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }
}
