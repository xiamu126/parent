package com.sybd.znld.model;

public enum MessageType implements IEnum {
    LAMP_ALARM(0), // 告警消息
    LAMP_STATISTIC(1),  // 数据统计消息
    LAMP_EXECUTION(2) // 运行消息，如果策略状态的改变
    ;
    MessageType(int v) {
        this.value = v;
    }
    private int value;
    @Override
    public int getValue() {
        return this.value;
    }
}
