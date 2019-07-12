package com.sybd.znld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

@Slf4j
@Getter @Setter
@ApiModel(value = "统一的接口返回数据")
public class ApiResult {
    @ApiModelProperty(value = "返回代码，0为成功，1为失败")
    public int code;
    @ApiModelProperty(value = "相关描述")
    public String msg;
    @ApiModelProperty(value = "具体的数据")
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
        List<ObjectError> errorList = bindingResult.getAllErrors();
        for(ObjectError error : errorList){
            log.debug(error.getDefaultMessage());
        }
        return ApiResult.fail("非法的参数");
    }

    public ApiResult(){}
    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.json = null;
    }
    public ApiResult(int code, String msg, Object json) {
        this.code = code;
        this.msg = msg;
        this.json = json;
    }
    @JsonIgnore
    public boolean isOk(){
        return code == 0;
    }
}
