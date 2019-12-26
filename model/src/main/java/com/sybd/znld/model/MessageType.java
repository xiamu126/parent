package com.sybd.znld.model;

public enum MessageType implements IEnum {
    LAMP_ALARM(0), // 告警消息
    LAMP_STATISTIC(1),  // 数据统计消息
    LAMP_EXECUTION(2), // 运行消息，如果策略状态的改变
    LAMP_ON_OFFLINE(3), // 设备上下线消息
    LAMP_ON_OFF_POWER(4), // 设备电源开关消息
    LAMP_ANGLE(5), // 倾斜角度消息
    LAMP_POSITION(6), //定位消息
    LAMP_ENVIRONMENT(7), // 环境消息
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
