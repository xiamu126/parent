package com.sybd.znld.model;

public enum Status implements IEnum {
    /*
    * 0 0x20 通用状态
    * 0x21 0x40 权限状态
    * 0x41 0x60 路灯状态
    * 0x61 0x70 单灯策略状态
    * */
    OK(Status.OK_CODE),
    DELETED(Status.DELETED_CODE),
    FROZEN(Status.FROZEN_CODE),

    AUTHORITY_OPERATION_ALLOW(Status.AUTHORITY_OPERATION_ALLOW_CODE),
    AUTHORITY_OPERATION_DENY(Status.AUTHORITY_OPERATION_DENY_CODE),

    LAMP_OK(Status.LAMP_OK_CODE),
    LAMP_ERROR(Status.LAMP_ERROR_CODE),
    LAMP_DEAD(Status.LAMP_DEAD_CODE),

    LAMP_STRATEGY_READY(Status.LAMP_STRATEGY_READY_CODE),
    LAMP_STRATEGY_RUNNING(Status.LAMP_STRATEGY_RUNNING_CODE),
    LAMP_STRATEGY_EXPIRED(Status.LAMP_STRATEGY_EXPIRED_CODE),
    LAMP_STRATEGY_TERMINATED(Status.LAMP_STRATEGY_TERMINATED_CODE);

    private static final int COMMON_CODE_MIN = 0;
    private static final int COMMON_CODE_MAX = 0x20;
    private static final int OK_CODE = COMMON_CODE_MIN;
    private static final int DELETED_CODE = COMMON_CODE_MIN+1;
    private static final int FROZEN_CODE = COMMON_CODE_MIN+2;
    public static boolean isValidCommonCode(int v){
        return v >= COMMON_CODE_MIN && v <= COMMON_CODE_MAX;
    }

    private static final int AUTHORITY_CODE_MIN = 0x21;
    private static final int AUTHORITY_CODE_MAX = 0x40;
    private static final int AUTHORITY_OPERATION_ALLOW_CODE = AUTHORITY_CODE_MIN;
    private static final int AUTHORITY_OPERATION_DENY_CODE = AUTHORITY_CODE_MIN+1;
    public static boolean isValidAuthorityCode(int v){
        return v >= AUTHORITY_CODE_MIN && v <= AUTHORITY_CODE_MAX;
    }

    private static final int LAMP_CODE_MIN = 0x41;
    private static final int LAMP_CODE_MAX = 0x60;
    private static final int LAMP_OK_CODE = LAMP_CODE_MIN;
    private static final int LAMP_ERROR_CODE = LAMP_CODE_MIN + 1;
    private static final int LAMP_DEAD_CODE = LAMP_CODE_MIN + 2;
    public static boolean isValidLampCode(int v){
        return v >= LAMP_CODE_MIN && v <= LAMP_CODE_MAX;
    }

    private static final int LAMP_STRATEGY_CODE_MIN = 0x61;
    private static final int LAMP_STRATEGY_CODE_MAX = 0x70;
    private static final int LAMP_STRATEGY_READY_CODE = LAMP_STRATEGY_CODE_MIN;
    private static final int LAMP_STRATEGY_RUNNING_CODE = LAMP_STRATEGY_CODE_MIN+1;
    private static final int LAMP_STRATEGY_EXPIRED_CODE = LAMP_STRATEGY_CODE_MIN+2; // 到时间终止
    private static final int LAMP_STRATEGY_TERMINATED_CODE = LAMP_STRATEGY_CODE_MIN+3; // 人为停止
    public static boolean isValidLampStrategyCode(int v){
        return v >= LAMP_STRATEGY_CODE_MIN && v <= LAMP_STRATEGY_CODE_MAX;
    }

    Status(int v){
        this.value = v;
    }
    private int value;
    @Override
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
}
