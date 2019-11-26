package com.sybd.znld.model.lamp;

import com.sybd.znld.model.IEnum;

public enum Strategy implements IEnum {
    LAMP(0), ELECTRICITY_DISPOSITION_BOX(1), MINI_STAR(2);
    Strategy(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue() {
        return this.value;
    }
}
