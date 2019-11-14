package com.sybd.znld.model;

public enum Status {
    OK(0), DELETED(1), FROZEN(2),
    OPERATION_ALLOW(3),  OPERATION_DENY(4);

    Status(int v){
        this.value = v;
    }
    private int value;
    public int getValue(){
        return this.value;
    }
    public static Status getStatus(int v){
        switch (v){
            case 0: return OK;
            case 1: return DELETED;
            case 2: return FROZEN;
            case 3: return OPERATION_ALLOW;
            case 4: return OPERATION_DENY;
        }
        return null;
    }
}
