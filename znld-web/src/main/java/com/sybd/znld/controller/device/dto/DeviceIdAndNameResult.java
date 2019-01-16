package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.service.dto.DeviceIdAndDeviceName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "获取设备Id与名字的返回值")
public class DeviceIdAndNameResult extends BaseApiResult {
    @ApiModelProperty(value = "设备Id与名字的集合")
    private List<DeviceIdAndDeviceName> deviceIdAndDeviceNames;

    public static DeviceIdAndNameResult fail(String msg){
        var tmp = new DeviceIdAndNameResult();
        tmp.setCode(1);
        tmp.setMsg(msg);
        return tmp;
    }

    public static DeviceIdAndNameResult success(List<DeviceIdAndDeviceName> result){
        var tmp = new DeviceIdAndNameResult();
        tmp.setCode(0);
        tmp.setDeviceIdAndDeviceNames(result);
        return tmp;
    }
}
