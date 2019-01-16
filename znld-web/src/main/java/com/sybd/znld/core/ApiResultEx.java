package com.sybd.znld.core;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiResultEx {
    private int code;
    private String msg;
    private Map<String, Object> json = new HashMap<>();

    public static ApiResult success(Map<String, Object> json){
        return new ApiResult(0, "", json);
    }
    public static ApiResult success(String msg, Map<String, Object> json){
        return new ApiResult(0, msg, json);
    }
}
