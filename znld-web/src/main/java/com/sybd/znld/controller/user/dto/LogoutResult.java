package com.sybd.znld.controller.user.dto;

import com.sybd.znld.core.BaseApiResult;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Slf4j
@ApiModel(value = "登出返回数据")
public class LogoutResult extends BaseApiResult {
    public static LogoutResult fail(String msg){
        var tmp = new LogoutResult();
        tmp.setCode(1);
        tmp.setMsg(msg);
        return tmp;
    }
    public static LogoutResult success(){
        var tmp = new LogoutResult();
        tmp.setCode(0);
        return tmp;
    }
}
