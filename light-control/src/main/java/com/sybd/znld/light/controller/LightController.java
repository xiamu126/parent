package com.sybd.znld.light.controller;

import com.sybd.znld.light.controller.dto.*;
import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.Pair;
import com.sybd.znld.model.lamp.Target;
import com.sybd.znld.model.onenet.dto.BaseResult;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/light")
public class LightController implements ILightController {
    private final IStrategyService strategyService;

    public LightController(IStrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @Override
    public BaseApiResult newLampStrategy(LampStrategy strategy) {
        try{
            if(this.strategyService.newLampStrategy(strategy) != null){
                return BaseApiResult.success("");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return BaseApiResult.fail("");
    }

    @Override
    public List<LampStrategy> getLampStrategies(String organId) {
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
    public Map<Target, List<OperationResult>> manualLampStrategy(ManualStrategy strategy) {
        if(!strategy.isValid()) return null;
        // 执行下发
        var map = this.strategyService.newLampManual(strategy);
        if(map == null || map.isEmpty()) return null;
        var ret = new HashMap<Target, List<OperationResult>>();
        for(var e : map.entrySet()) {
            var tmpList = e.getValue().stream().map(p -> {
                var tmp = new OperationResult();
                tmp.id = p.one;
                tmp.code = p.two.errno;
                return tmp;
            }).collect(Collectors.toList());
            ret.put(e.getKey(), tmpList);
        }
        return ret;
    }

    @Override
    public Map<Target, List<OperationResult>> manualLampBrightnessStrategy(ManualStrategy strategy) {
        if(!strategy.isValid()) return null;
        // 执行下发
        var map = this.strategyService.newLampManualBrightness(strategy);
        if(map == null || map.isEmpty()) return null;
        var ret = new HashMap<Target, List<OperationResult>>();
        for(var e : map.entrySet()) {
            var tmpList = e.getValue().stream().map(p -> {
                var tmp = new OperationResult();
                tmp.id = p.one;
                tmp.code = p.two.errno;
                return tmp;
            }).collect(Collectors.toList());
            ret.put(e.getKey(), tmpList);
        }
        return ret;
    }

    @Override
    public BaseApiResult newBoxStrategy(BoxStrategy strategy) {
        try{
            if(this.strategyService.newBoxStrategy(strategy) != null){
                return BaseApiResult.success("");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }

        return BaseApiResult.fail("");
    }

    @Override
    public List<BoxStrategy> getBoxStrategies(String organId) {
        if(MyString.isEmptyOrNull(organId)){
            return null;
        }
        try{
            return this.strategyService.getBoxStrategies(organId);
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    @Override
    public BaseApiResult manualBoxStrategy(ManualStrategy strategy) {
        if(!strategy.isValid()) return BaseApiResult.fail("");
        // 执行下发
        return null;
    }
}
