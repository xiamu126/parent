package com.sybd.znld.model;

public class BaseStatus{
    public static final short OK = 0;
    public static final short FROZEN = 1;
    public static final short DELETED = 2;

    public static boolean isValid(short v){
        switch (v){
            case OK:
            case FROZEN:
            case DELETED:
                return true;
            default:
                return false;
        }
    }
}
