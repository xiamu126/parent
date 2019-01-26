package com.sybd.znld.controller.device.dto;

import com.sybd.znld.core.BaseApiResult;
import com.sybd.znld.service.dto.DeviceIdAndDeviceName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "获取设备Id与名字的返回值")
class DeviceIdAndNameResult(code: Int, msg: String): BaseApiResult(code, msg) {
    @ApiModelProperty(value = "设备Id与名字的集合")
    private var deviceIdAndDeviceNames: List<DeviceIdAndDeviceName>? = null;

    companion object {
        @JvmStatic fun fail(msg: String): DeviceIdAndNameResult{
            return DeviceIdAndNameResult(1, msg)
        }

        @JvmStatic fun success(result: List<DeviceIdAndDeviceName>): DeviceIdAndNameResult{
            val tmp = DeviceIdAndNameResult(0, "");
            tmp.deviceIdAndDeviceNames= result
            return tmp;
        }
    }
}
