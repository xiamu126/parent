package com.sybd.znld.light.control;

import com.sybd.znld.light.control.dto.BaseStrategy;
import com.sybd.znld.light.control.dto.LampStrategy;
import com.sybd.znld.light.control.dto.ManualBrightnessStrategy;
import com.sybd.znld.light.control.dto.ManualStrategy;
import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            if(this.strategyService.newLampStrategy(strategy)){
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
    public BaseApiResult manualLampStrategy(ManualStrategy strategy) {
        return BaseApiResult.fail("");
    }

    @Override
    public BaseApiResult manualLampBrightnessStrategy(ManualBrightnessStrategy strategy) {
        return BaseApiResult.fail("");
    }

    @Override
    public BaseApiResult newBoxStrategy(BaseStrategy strategy) {
        try{
            if(this.strategyService.newBoxStrategy(strategy)){
                return BaseApiResult.success("");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }

        return BaseApiResult.fail("");
    }

    @Override
    public List<BaseStrategy> getBoxStrategies(String organId) {
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
        return BaseApiResult.fail("");
    }
}
