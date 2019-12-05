package com.sybd.znld.model;

public enum StrategyStatus implements IEnum {
    READY(0),
    RUNNING(1),
    EXPIRED(2),
    TERMINATED(3),
    PARTIALLY_RUNNING(4);

    StrategyStatus(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue(){
        return this.value;
    }
}
