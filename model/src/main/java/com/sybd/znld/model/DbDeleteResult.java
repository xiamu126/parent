package com.sybd.znld.model;

public enum DbDeleteResult {
    PARAM_ERROR(0),
    CASCADE_ERROR(1),
    NOT_FOUND(2),
    SUCCESS(3),
    ALREADY_DELETED(4);
    private int value;
    DbDeleteResult(int value){
        this.value = value;
    }
    public boolean isSuccess(){
        return this.value == 3;
    }
}
