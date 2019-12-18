package com.sybd.znld.model;

@MyEnum
public enum DeviceStatus implements IEnum {
    OK(0),
    ERROR(1),
    DEAD(2),
    ;
    DeviceStatus(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue(){
        return this.value;
    }
}
