package com.sybd.znld.service.znld.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@ApiModel(value = "选中资源的单元值")
public class CheckedResource implements Serializable {
    @ApiModelProperty(value = "元素Id")
    public String oneNetKey;
    @ApiModelProperty(value = "元素的名字")
    public String name;
    @ApiModelProperty(value = "元素的描述")
    public String description;
}
