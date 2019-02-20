package com.sybd.znld.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "设备Id与名字的单元值")
public class DeviceIdAndDeviceName implements Serializable {
    @ApiModelProperty(value = "设备Id")
    public Integer deviceId;
    @ApiModelProperty(value = "设备名字")
    public String deviceName;

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
