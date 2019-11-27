package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IEnum;

public enum UserStatus implements IEnum {
    OK(0), FROZEN(1);
    UserStatus(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue() {
        return this.value;
    }
}
