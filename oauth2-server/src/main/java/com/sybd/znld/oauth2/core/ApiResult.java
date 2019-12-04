package com.sybd.znld.oauth2.core;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

@Slf4j
public class ApiResult {
    public int code;
    public String msg;
    public Object json;

    public static ApiResult success(){
        return new ApiResult(0,"","");
    }
    public static ApiResult success(String msg){
        return new ApiResult(0, msg, "");
    }
    public static ApiResult success(Object json){
        return new ApiResult(0, "", json);
    }
    public static ApiResult success(String msg, Object json){
        return new ApiResult(0, msg, json);
    }

    public static ApiResult fail(){
        return new ApiResult(1,"","");
    }
    public static ApiResult fail(String msg){
        return new ApiResult(1, msg,"");
    }
    public static ApiResult fail(BindingResult bindingResult){
        var errorList = bindingResult.getAllErrors();
        for(var error : errorList){
            log.debug(error.getDefaultMessage());
        }
        return ApiResult.fail("非法的参数");
    }

    public ApiResult(int code, String msg, Object json) {
        this.code = code;
        this.msg = msg;
        this.json = json;
    }

}
