package com.sybd.znld.light.controller;

import com.sybd.znld.model.lamp.dto.LampStatistic;
import com.sybd.znld.model.lamp.dto.RegionBoxLamp;
import com.sybd.znld.model.lamp.dto.Message;
import com.sybd.znld.model.lamp.dto.OpResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

public interface IDeviceController {
    // 打开关闭设备电源
    @PutMapping(value = "power/{imei}/{data_stream}/{action}", produces = {MediaType.APPLICATION_JSON_VALUE})
    OpResult operateDevice(@PathVariable("imei") String imei,
                           @PathVariable(name = "data_stream") String dataStream,
                           @PathVariable("action") Message.CommonAction action);
    // 关闭设备电源（旧版）
    @PutMapping(value = "power/off/{imei}/{data_stream}", produces = {MediaType.APPLICATION_JSON_VALUE})
    OpResult offDevice(@PathVariable("imei") String imei, @PathVariable(name = "data_stream") String dataStream);
    // 打开设备电源（旧版）
    @PutMapping(value = "power/on/{imei}/{data_stream}", produces = {MediaType.APPLICATION_JSON_VALUE})
    OpResult onDevice(@PathVariable("imei") String imei, @PathVariable(name = "data_stream") String dataStream);

    // 查询设备开关状态
    @GetMapping(value = "power/{imei}/{data_stream}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Boolean isDeviceOn(@PathVariable("imei") String imei, @PathVariable(name = "data_stream") String dataStream);

    // 获取某个区域下的所有路灯
    @GetMapping(value = "lamp/{organ_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<RegionBoxLamp> getLamps(@PathVariable("organ_id") String organId);


    // 手动获取一次最近的统计信息
    @GetMapping(value = "lamp/statistics/{lampId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    LampStatistic getStatistics(@PathVariable("lampId") String lampId);
}
