package com.sybd.znld.light.controller.dto;

public enum DeviceAction {
    OPEN("on"), CLOSE("off")
    ;
    private Object value;
    DeviceAction(Object v) {
        this.value = v;
    }
    public Object getValue() {
        return this.value;
    }
}
