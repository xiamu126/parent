package com.sybd.znld.environment.controller;

import com.sybd.znld.environment.service.dto.AQIResult;
import com.sybd.znld.environment.service.dto.AVGResult;
import com.sybd.znld.model.lamp.dto.LampWithLocation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface IRegionController {
    // 获取某个组织的可能的环境监测点
    @GetMapping(value="{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<LampWithLocation> getRegionOfEnvironmentList(@PathVariable(name = "organId") String organId, HttpServletRequest request);

    // 获取某个分平台的AQI（时报）
    @GetMapping(value="report/aqi/organ/{organId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    AQIResult getAQILastHourOfOrgan(@PathVariable(name = "organId") String organId);
    // 获取某个监测点的AQI（时报）
    @GetMapping(value="report/aqi/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    AQIResult getAQILastHourOfDevice(@PathVariable(name = "deviceId") Integer deviceId);
    // 获取某个监测点的AQI（日报）
    @GetMapping(value="report/aqi/day/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    AQIResult getAQILastDayOfDevice(@PathVariable(name = "deviceId") Integer deviceId);

    // AQI历史数据按小时来
    @GetMapping(value="report/aqi/hourly/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<AQIResult> getAQIHourlyOfDevice(@PathVariable(name = "deviceId") Integer deviceId);
    // AQI历史数据按天来
    @GetMapping(value="report/aqi/daily/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<AQIResult> getAQIDailyOfDevice(@PathVariable(name = "deviceId") Integer deviceId);
    // AQI历史数据按月来
    @GetMapping(value="report/aqi/monthly/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<AQIResult> getAQIMonthlyOfDevice(@PathVariable(name = "deviceId") Integer deviceId);

    // AQI历史数据按小时来，指定时间区间
    @GetMapping(value="report/aqi/hourly/{deviceId}/{begin}/{end}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<AQIResult> getAQIHourlyOfDeviceBetween(@PathVariable(name = "deviceId") Integer deviceId,
                                                @PathVariable(name = "begin") Long begin,
                                                @PathVariable(name = "end") Long end);
    // AQI历史数据按天来，指定时间区间
    @GetMapping(value="report/aqi/daily/{deviceId}/{begin}/{end}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<AQIResult> getAQIDailyOfDeviceBetween(@PathVariable(name = "deviceId") Integer deviceId,
                                               @PathVariable(name = "begin") Long begin,
                                               @PathVariable(name = "end") Long end);
    // AQI历史数据按月来，指定时间区间
    @GetMapping(value="report/aqi/monthly/{deviceId}/{begin}/{end}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<AQIResult> getAQIMonthlyOfDeviceBetween(@PathVariable(name = "deviceId") Integer deviceId,
                                                 @PathVariable(name = "begin") Long begin,
                                                 @PathVariable(name = "end") Long end);

    // 元素历史数据按小时来
    @GetMapping(value="report/hourly/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, List<AVGResult>> getAvgHourlyOfDevice(@PathVariable(name = "deviceId") Integer deviceId);
    // 元素历史数据按天来
    @GetMapping(value="report/daily/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, List<AVGResult>> getAvgDailyOfDevice(@PathVariable(name = "deviceId") Integer deviceId);
    // 元素历史数据按月来
    @GetMapping(value="report/monthly/{deviceId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, List<AVGResult>> getAvgMonthlyOfDevice(@PathVariable(name = "deviceId") Integer deviceId);

    // 元素历史数据按小时来
    @GetMapping(value="report/hourly/{deviceId}/{begin}/{end}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, List<AVGResult>> getAvgHourlyOfDeviceBetween(@PathVariable(name = "deviceId") Integer deviceId,
                                                             @PathVariable(name = "begin") Long begin,
                                                             @PathVariable(name = "end") Long end);
    // 元素历史数据按天来
    @GetMapping(value="report/daily/{deviceId}/{begin}/{end}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, List<AVGResult>> getAvgDailyOfDeviceBetween(@PathVariable(name = "deviceId") Integer deviceId,
                                                            @PathVariable(name = "begin") Long begin,
                                                            @PathVariable(name = "end") Long end);
    // 元素历史数据按月来
    @GetMapping(value="report/monthly/{deviceId}/{begin}/{end}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Map<String, List<AVGResult>> getAvgMonthlyOfDeviceBetween(@PathVariable(name = "deviceId") Integer deviceId,
                                                              @PathVariable(name = "begin") Long begin,
                                                              @PathVariable(name = "end") Long end);
}
