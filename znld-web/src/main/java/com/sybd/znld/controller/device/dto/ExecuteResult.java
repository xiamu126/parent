package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.OneNetExecuteResult;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "执行命令后的返回值")
public class ExecuteResult extends BaseApiResult{
    public ExecuteResult(){}
    public ExecuteResult(Integer code, String msg){
        super(code, msg);
    }
    public static ExecuteResult fail(String msg){
        return new ExecuteResult(1, msg);
    }
    public static ExecuteResult success(OneNetExecuteResult result){
        return new ExecuteResult(0, "");
    }
}
