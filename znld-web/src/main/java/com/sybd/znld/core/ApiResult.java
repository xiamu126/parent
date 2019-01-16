package com.sybd.znld.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@ApiModel(value = "统一的接口返回数据")
public class ApiResult {
    @ApiModelProperty(value = "返回代码，0为成功，1为失败")
    private int code;
    @ApiModelProperty(value = "相关描述")
    private String msg;
    @ApiModelProperty(value = "具体的数据")
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
