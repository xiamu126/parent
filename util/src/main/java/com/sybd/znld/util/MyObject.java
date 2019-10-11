package com.sybd.znld.util;

import java.util.Arrays;
import java.util.Objects;

public final class MyObject {
    public static boolean isAllNull(Object ...params){
        return Arrays.stream(params).allMatch(Objects::isNull);
    }
    public static boolean isAnyNull(Object ...params){
        return Arrays.stream(params).anyMatch(Objects::isNull);
    }
}
