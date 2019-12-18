package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.MyEnum;

@MyEnum
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
