package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IEnum;

public enum Target implements IEnum {
    LAMP(0), REGION(1);
    Target(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue() {
        return this.value;
    }
}
