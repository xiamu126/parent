package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IEnum;

public enum Target implements IEnum {
    REGION(0), SINGLE(1);
    Target(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue() {
        return this.value;
    }
}
