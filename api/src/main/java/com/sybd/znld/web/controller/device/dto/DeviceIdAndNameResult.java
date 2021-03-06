package com.sybd.znld.web.controller.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.DeviceIdAndDeviceName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@ApiModel(value = "获取设备Id与名字的返回值")
@Getter @Setter
@NoArgsConstructor
public class DeviceIdAndNameResult extends BaseApiResult {
    @ApiModelProperty(value = "设备Id与名字的集合")
    @JsonProperty("devices")
    public List<DeviceIdAndDeviceName> deviceIdAndDeviceNames;

    public DeviceIdAndNameResult(Integer code, String msg){
        super(code, msg);
    }
    public DeviceIdAndNameResult(Integer code, String msg, List<DeviceIdAndDeviceName> deviceIdAndDeviceNames){
        super(code, msg);
        this.deviceIdAndDeviceNames = deviceIdAndDeviceNames;
    }

    public static DeviceIdAndNameResult fail(String msg){
        return new DeviceIdAndNameResult(1, msg);
    }
    public static DeviceIdAndNameResult success(List<DeviceIdAndDeviceName> deviceIdAndDeviceNames){
        return new DeviceIdAndNameResult(0, "", deviceIdAndDeviceNames);
    }
}