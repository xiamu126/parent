package com.sybd.znld.light.controller;

import com.sybd.znld.model.lamp.dto.OpResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

public interface IDeviceController {
    // 打开设备电源
    @PutMapping(value = "status/device/{deviceId:^[1-9]\\d*$}/{dataStream}", produces = {MediaType.APPLICATION_JSON_VALUE})
    OpResult openDevice(@PathVariable("deviceId") Integer deviceId, @PathVariable(name = "dataStream") String dataStream);
    // 打开设备电源
    @PutMapping(value = "status/device/{deviceId:^[1-9]\\d*$}/{dataStream}", produces = {MediaType.APPLICATION_JSON_VALUE})
    OpResult CloseDevice(@PathVariable("deviceId") Integer deviceId, @PathVariable(name = "dataStream") String dataStream);
    // 查询设备开关状态
}
