package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.MyEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LampExecutionModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String lampId; // 照明灯id
    public String organId;
    public Mode mode;
    public String lampStrategyId; // 关联了哪个策略，在手动模式下，这个为空字符串
    public Status status; // 策略的执行状态
    public Integer tryingCount = 0; // 重试了几次
    public LocalDateTime lastTryingTime = LocalDateTime.of(1973,1,1,0,0,0); // 最后一次重试时间
    public LocalDateTime lastUpdateTime = LocalDateTime.of(1973,1,1,0,0,0); // 最后一次更新时间

    @MyEnum
    public enum Status implements IEnum {
        SUCCESS(0),
        FAILED(1),
        TRYING(2),
        TRYING_FAILED(3),
        TRYING_SUCCESS(4),
        EXPIRED_FAILED(5),
        TRYING_INTERRUPTED(6),
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

    @MyEnum
    public enum Mode implements IEnum {
        MANUAL(0), STRATEGY(1)
        ;
        Mode(int v) {
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }
}
