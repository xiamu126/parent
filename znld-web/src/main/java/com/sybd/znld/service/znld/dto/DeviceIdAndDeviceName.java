package com.sybd.znld.service.znld.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@ApiModel(value = "设备Id与名字的单元值")
public class DeviceIdAndDeviceName implements Serializable {
    @ApiModelProperty(value = "设备Id")
    public Integer deviceId;
    @ApiModelProperty(value = "设备名字")
    public String deviceName;
}
