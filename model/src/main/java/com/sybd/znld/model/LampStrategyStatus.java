package com.sybd.znld.model;

@MyEnum
public enum LampStrategyStatus implements IEnum {
    READY(0),
    RUNNING(1),
    EXPIRED(2),
    TERMINATED(3),
    PARTIALLY_RUNNING(4),
    OVERLAPPED(5)
    ;

    LampStrategyStatus(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue(){
        return this.value;
    }
}
