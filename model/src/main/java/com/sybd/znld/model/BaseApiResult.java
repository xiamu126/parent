package com.sybd.znld.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@ApiModel(value = "统一的接口返回数据")
@Getter @Setter @NoArgsConstructor
public class BaseApiResult implements Serializable{
    @ApiModelProperty(value = "返回代码，0为成功，1为失败")
    public Integer code;
    @ApiModelProperty(value = "相关描述")
    public String msg;
    @JsonIgnore
    public boolean isOk(){
        return code == 0;
    }
    public BaseApiResult(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public BaseApiResult(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public static BaseApiResult fail(String msg){
        return new BaseApiResult(1, msg);
    }
    public static BaseApiResult fail(){
        return new BaseApiResult(1, "");
    }
    public static BaseApiResult success(String msg) {
        return new BaseApiResult(0, msg);
    }
    public static BaseApiResult success() {
        return new BaseApiResult(0, "");
    }
}