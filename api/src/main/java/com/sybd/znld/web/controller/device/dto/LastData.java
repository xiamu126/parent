package com.sybd.znld.web.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel(value = "内部使用，包含最新数据的更新时间及当前数值")
@NoArgsConstructor
@Getter
@Setter
public class LastData {
    @ApiModelProperty(value = "最后的更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "CN")
    public LocalDateTime updateAt;
    @ApiModelProperty(value = "当前的数值")
    public String currentValue;
}
