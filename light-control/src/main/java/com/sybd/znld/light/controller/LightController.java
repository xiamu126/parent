package com.sybd.znld.light.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.light.service.IReportService;
import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.model.lamp.dto.Report;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.dto.*;
import com.sybd.znld.model.onenet.Config;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/light")
public class LightController implements ILightController {
    private final IStrategyService strategyService;
    private final IReportService reportService;
    private final ObjectMapper objectMapper;

    @Autowired
    public LightController(IStrategyService strategyService, IReportService reportService, ObjectMapper objectMapper) {
        this.strategyService = strategyService;
        this.reportService = reportService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean isLampStrategyOverlapping(LampStrategy strategy) {
        return this.strategyService.isLampStrategyOverlapping(strategy);
    }

    @Override
    public BaseApiResult newLampStrategy(LampStrategy strategy) {
        try{
            var ret = this.strategyService.newLampStrategy(strategy);
            if(ret != null && ret.isOk()){
                return BaseApiResult.success("");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return BaseApiResult.fail("");
    }

    @Override
    public Map<String, BaseApiResult> executeLampStrategy(LampStrategyCmd cmd) {
        return this.strategyService.executeLampStrategy(cmd);
    }

    @Override
    public List<LampStrategyOutput> getLampStrategies(String organId) {
        if(MyString.isEmptyOrNull(organId)){
            return null;
        }
        try{
            return this.strategyService.getLampStrategies(organId);
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    @Override
    public Map<String, BaseApiResult> executeLampManualCommand(LampManualCmd cmd) {
        if(!cmd.isValid()) return null;
        // 如果设备已经有策略在运行或等待，则返回一个告警
        // 执行下发
        return this.strategyService.executeLampStrategy(cmd);
    }

    @Override
    public Report getReportThisWeek(String organId) {
        return this.reportService.getReport(organId, Report.TimeType.WEEK);
    }

    @Override
    public Report getReportThisMonth(String organId) {
        return this.reportService.getReport(organId, Report.TimeType.MONTH);
    }

    @Override
    public Report getReportThisYear(String organId) {
        return this.reportService.getReport(organId, Report.TimeType.YEAR);
    }

    @Override
    public Report getReportBetween(String organId, Long beginTimestamp, Long endTimestamp) {
        var begin = MyDateTime.toLocalDateTime(beginTimestamp);
        var end = MyDateTime.toLocalDateTime(endTimestamp);
        if(begin != null && end != null) {
            return this.reportService.getReport(organId, Report.TimeType.YEAR, begin, end);
        }
        return null;
    }

    @Override
    public List<LampAlarm.Message> getAlarmList(String organId) {
        return this.reportService.getAlarmList(organId);
    }

    @Override
    public BaseApiResult deleteLampStrategy(@PathVariable(name = "strategyId") String strategyId) {
        return this.strategyService.deleteLampStrategy(strategyId);
    }
}
