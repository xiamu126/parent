package com.sybd.znld.onenet.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OneNetExecuteArgs {
    @ApiModelProperty(value = "具体的命令")
    protected String args = "SYBD";
}
