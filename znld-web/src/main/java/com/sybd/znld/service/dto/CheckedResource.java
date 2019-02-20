package com.sybd.znld.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "选中资源的单元值")
public class CheckedResource implements Serializable {
    @ApiModelProperty(value = "元素Id")
    public String oneNetKey;
    @ApiModelProperty(value = "元素的名字")
    public String name;
    @ApiModelProperty(value = "元素的描述")
    public String description;

    public String getOneNetKey() {
        return oneNetKey;
    }

    public void setOneNetKey(String oneNetKey) {
        this.oneNetKey = oneNetKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
