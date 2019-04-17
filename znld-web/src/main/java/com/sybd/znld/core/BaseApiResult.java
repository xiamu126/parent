package com.sybd.znld.core;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@ApiModel(value = "统一的接口返回数据")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BaseApiResult implements Serializable{
    @ApiModelProperty(value = "返回代码，0为成功，1为失败")
    public Integer code;
    @ApiModelProperty(value = "相关描述")
    public String msg;

    public boolean isOk(){
        return code == 0;
    }
}