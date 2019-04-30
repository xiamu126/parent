package com.sybd.znld.znld.core;

import java.util.HashMap;
import java.util.Map;

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getJson() {
        return json;
    }

    public void setJson(Map<String, Object> json) {
        this.json = json;
    }
}
