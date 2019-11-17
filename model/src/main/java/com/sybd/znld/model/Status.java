package com.sybd.znld.model;

public enum Status {
    /*
    * 0 0x20 通用状态
    * 0x21 0x40 权限状态
    * 0x41 0x60 路灯状态
    * */
    OK(Status.OK_CODE), DELETED(Status.DELETED_CODE), FROZEN(Status.FROZEN_CODE),
    AUTHORITY_OPERATION_ALLOW(Status.AUTHORITY_OPERATION_ALLOW_CODE),  AUTHORITY_OPERATION_DENY(Status.AUTHORITY_OPERATION_DENY_CODE),
    LAMP_ERROR(Status.LAMP_ERROR_CODE), LAMP_DEAD(Status.LAMP_DEAD_CODE);

    private static final int OK_CODE = 0;
    private static final int DELETED_CODE = 0x1;
    private static final int FROZEN_CODE = 0x2;
    private static final int AUTHORITY_OPERATION_ALLOW_CODE = 0x21;
    private static final int AUTHORITY_OPERATION_DENY_CODE = 0x22;
    private static final int LAMP_ERROR_CODE = 0x41;
    private static final int LAMP_DEAD_CODE = 0x42;

    private static final int COMMON_CODE_MIN = 0;
    private static final int COMMON_CODE_MAX = 0x20;
    private static final int AUTHORITY_CODE_MIN = 0x21;
    private static final int AUTHORITY_CODE_MAX = 0x40;
    private static final int LAMP_CODE_MIN = 0x41;
    private static final int LAMP_CODE_MAX = 0x60;

    Status(int v){
        this.value = v;
    }
    private int value;
    public int getValue(){
        return this.value;
    }
    public static Status getStatus(int v){
        var status = Status.class.getEnumConstants();
        for(var s : status){
            if(s.value == v) return s;
        }
        return null;
    }

    public static boolean isValidAuthorityStatusCode(int v){
        return v >= COMMON_CODE_MIN && v <= COMMON_CODE_MAX;
    }
    public static boolean isValidLampStatusCode(int v){
        return v >= AUTHORITY_CODE_MIN && v <= AUTHORITY_CODE_MAX;
    }
}
