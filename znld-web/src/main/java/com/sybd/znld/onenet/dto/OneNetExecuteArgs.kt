package com.sybd.znld.onenet.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

open class OneNetExecuteArgs constructor(@ApiModelProperty(value = "具体的命令") var args: String = "SYBD")
