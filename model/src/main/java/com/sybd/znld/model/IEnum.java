package com.sybd.znld.model;

public interface IEnum {
    int getValue();
    static <E extends Enum & IEnum> E getEnum(Class<E> enumClass, int v){
        var enums = enumClass.getEnumConstants();
        for(var e : enums){
            if(e.getValue() == v) return e;
        }
        return null;
    }
}
