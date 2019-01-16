package com.sybd.znld.core;

import com.sybd.znld.controller.device.dto.HistoryDataResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "统一的接口返回数据")
public class BaseApiResult implements Serializable {
    @ApiModelProperty(value = "返回代码，0为成功，1为失败")
    private int code;
    @ApiModelProperty(value = "相关描述")
    private String msg;
}