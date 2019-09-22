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
    public Object data;

    public static ApiResult success(){
        return new ApiResult(0,"",null);
    }
    public static ApiResult success(String msg){
        return new ApiResult(0, msg, null);
    }
    public static ApiResult success(Object data){
        return new ApiResult(0, "", data);
    }
    public static ApiResult success(String msg, Object data){
        return new ApiResult(0, msg, data);
    }

    public static ApiResult fail(){
        return new ApiResult(1,"",null);
    }
    public static ApiResult fail(String msg){
        return new ApiResult(1, msg,null);
    }
    public static ApiResult fail(String msg, Object data){
        return new ApiResult(1, msg, data);
    }

    public ApiResult(){}
    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }
    public ApiResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    @JsonIgnore
    public boolean isOk(){
        return code == 0;
    }
}
