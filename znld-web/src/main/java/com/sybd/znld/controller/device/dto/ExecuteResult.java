package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.onenet.dto.OneNetExecuteResult;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@ApiModel(value = "执行命令后的返回值")
public class ExecuteResult extends BaseApiResult {
    public static ExecuteResult fail(String msg){
        var tmp = new ExecuteResult();
        tmp.setCode(1);
        tmp.setMsg(msg);
        return tmp;
    }
    public static ExecuteResult success(OneNetExecuteResult result){
        var tmp = new ExecuteResult();
        tmp.setCode(0);
        return tmp;
    }
}
