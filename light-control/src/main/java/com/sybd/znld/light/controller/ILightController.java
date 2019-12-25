package com.sybd.znld.light.controller;

import com.sybd.znld.model.lamp.dto.Report;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface ILightController {
    @PostMapping(value = "strategy/lamp/check", produces = {MediaType.APPLICATION_JSON_VALUE})
    Boolean isLampStrategyOverlapping(@RequestBody LampStrategy strategy);

    // 新建照明灯策略
    @PostMapping(value = "strategy/lamp", produces = {MediaType.APPLICATION_JSON_VALUE})
    BaseApiResult newLampStrategy(@RequestBody LampStrategy strategy);
    // 删除照明灯策略
    @DeleteMapping(value = "strategy/lamp/{strategyId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    BaseApiResult deleteLampStrategy(@PathVariable(name = "strategyId") String strategyId);

    // 对目标对象执行照明灯策略计划
    @PostMapping(value = "strategy/lamp/schedule", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, BaseApiResult> executeLampStrategy(@RequestBody LampStrategyCmd cmd);
    // 对目标对象（照明灯）执行手动控制
    @PostMapping(value = "manual/lamp", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, BaseApiResult> executeLampManualCommand(@RequestBody LampManualCmd cmd);

    // 获取某个分平台下的照明灯策略
    @GetMapping(value = "strategy/lamp/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<LampStrategyOutput> getLampStrategies(@PathVariable(name = "organId") String organId);


    // 获取本周的统计，包括电量，上线率，亮灯率，故障率
    @GetMapping(value = "report/week/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Report getReportThisWeek(@PathVariable(name = "organId") String organId);

    // 获取本月的统计，包括电量，上线率，亮灯率，故障率
    @GetMapping(value = "report/month/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Report getReportThisMonth(@PathVariable(name = "organId") String organId);

    // 获取本年的统计，包括电量，上线率，亮灯率，故障率
    @GetMapping(value = "report/year/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Report getReportThisYear(@PathVariable(name = "organId") String organId);

    // 获取某个时间区间内的统计，包括电量，上线率，亮灯率，故障率
    @GetMapping(value = "report/year/{organId:^[0-9a-f]{32}$}/{begin}/{end}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Report getReportBetween(@PathVariable(name = "organId") String organId,
                            @PathVariable(name = "begin") Long beginTimestamp,
                            @PathVariable(name = "end") Long endTimestamp);

    // 获取某个分平台的报警数据
    @GetMapping(value = "alarm/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<LampAlarmOutput> getAlarmList(@PathVariable(name = "organId") String organId);
}
