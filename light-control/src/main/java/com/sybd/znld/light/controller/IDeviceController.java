package com.sybd.znld.light.controller;

import com.sybd.znld.light.controller.dto.Command;
import com.sybd.znld.light.controller.dto.DeviceAction;
import com.sybd.znld.light.controller.dto.RegionBoxLamp;
import com.sybd.znld.model.lamp.dto.OpResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

public interface IDeviceController {
    // 打开关闭设备电源
    @PutMapping(value = "status/{imei}/{data_stream}/{action}", produces = {MediaType.APPLICATION_JSON_VALUE})
    OpResult operateDevice(@PathVariable("imei") String imei,
                           @PathVariable(name = "data_stream") String dataStream,
                           @PathVariable("action") DeviceAction action);

    // 查询设备开关状态

    // 获取某个区域下的所有路灯
    @GetMapping(value = "lamp/{organ_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<RegionBoxLamp> getLamps(@PathVariable("organ_id") String organId);
}
