package com.sybd.znld.model;

public enum StrategyFailedStatus implements IEnum {
    TRYING(0), TRYING_MAX_QUIT(1), SUCCESS(2)
    ;

    StrategyFailedStatus(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue(){
        return this.value;
    }
}
