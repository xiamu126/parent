package com.sybd.znld.model;

public enum  DbDeleteResult {
    PARAM_ERROR(DbDeleteResult.PARAM_ERROR_CODE),
    CASCADE_ERROR(DbDeleteResult.CASCADE_ERROR_CODE),
    NOT_FOUND(DbDeleteResult.NOT_FOUND_CODE),
    SUCCESS(DbDeleteResult.SUCCESS_CODE),
    ALREADY_DELETED(DbDeleteResult.ALREADY_DELETED_CODE);
    private int value;
    private static final int PARAM_ERROR_CODE = 0;
    private static final int CASCADE_ERROR_CODE = 1;
    private static final int NOT_FOUND_CODE = 2;
    private static final int SUCCESS_CODE = 3;
    private static final int ALREADY_DELETED_CODE = 4;
    DbDeleteResult(int value){
        this.value = value;
    }
    public boolean isSuccess(){
        return this.value == DbDeleteResult.SUCCESS_CODE;
    }
}
