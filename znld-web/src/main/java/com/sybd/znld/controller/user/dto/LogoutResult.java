package com.sybd.znld.controller.user.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;


@ApiModel(value = "登出返回数据")
public class LogoutResult extends BaseApiResult{
    public LogoutResult(){}
    public LogoutResult(Integer code, String msg){
        super(code, msg);
    }
    public static LogoutResult fail(String msg){
        return new LogoutResult(1, msg);
    }
    public static LogoutResult success(){
        return new LogoutResult(0, "");
    }
}
