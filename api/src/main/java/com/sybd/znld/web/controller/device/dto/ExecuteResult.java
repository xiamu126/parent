package com.sybd.znld.web.controller.device.dto;

import com.sybd.znld.model.BaseApiResult;
import io.swagger.annotations.ApiModel;
import lombok.NoArgsConstructor;

@ApiModel(value = "执行命令后的返回值")
@NoArgsConstructor
public class ExecuteResult extends BaseApiResult {
    public ExecuteResult(Integer code, String msg){
        super(code, msg);
    }
    public static ExecuteResult fail(String msg){
        return new ExecuteResult(1, msg);
    }
    public static ExecuteResult success(){
        return new ExecuteResult(0, "");
    }
    public static ExecuteResult success(String msg){
        return new ExecuteResult(0, msg);
    }
}
