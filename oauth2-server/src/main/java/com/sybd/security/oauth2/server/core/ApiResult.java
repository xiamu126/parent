package com.sybd.security.oauth2.server.core;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ApiResult {
    private int code;
    private String msg;
    private Object json;

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
}
