package com.sybd.znld.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
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

    public ApiResultEx(){}
    public ApiResultEx(int code, String msg, Map<String, Object> json) {
        this.code = code;
        this.msg = msg;
        this.json = json;
    }
}
