package com.sybd.znld.service.znld.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@ApiModel(value = "选中资源的单元值")
public class CheckedResource implements Serializable {
    @ApiModelProperty(value = "元素Id")
    @JsonProperty("resourceKey")
    public String dataStreamId;
    @ApiModelProperty(value = "元素的描述")
    public String description;
}
