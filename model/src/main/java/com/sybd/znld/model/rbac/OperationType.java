package com.sybd.znld.model.rbac;

import com.sybd.znld.model.IEnum;

public enum OperationType implements IEnum {
    API(0),WEB(1);
    OperationType(int v){
        this.value = v;
    }
    private int value;
    @Override
    public int getValue() {
        return this.value;
    }
}
