package com.sybd.znld.core;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "统一的接口返回数据")
public class BaseApiResult implements Serializable{
    @ApiModelProperty(value = "返回代码，0为成功，1为失败")
    public Integer code;
    @ApiModelProperty(value = "相关描述")
    public String msg;


    public BaseApiResult(){}
    public BaseApiResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}