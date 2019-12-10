package com.sybd.znld.model;

public enum MessageType implements IEnum {
    LAMP_ALARM(0), LAMP_STATISTIC(1)
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
