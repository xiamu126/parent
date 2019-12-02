package com.sybd.znld.light.controller;

import com.sybd.znld.light.controller.dto.*;
import com.sybd.znld.model.BaseApiResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ILightController {
    // 下发照明灯策略
    @PostMapping(value = "strategy/lamp", produces = {MediaType.APPLICATION_JSON_VALUE})
    BaseApiResult newLampStrategy(@RequestBody LampStrategy strategy);

    @GetMapping(value = "strategy/lamp/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<LampStrategy> getLampStrategies(@PathVariable(name = "organId") String organId);

    // 手动打开关闭照明灯
    @PostMapping(value = "strategy/manual/lamp", produces = {MediaType.APPLICATION_JSON_VALUE})
    BaseApiResult manualLampStrategy(@RequestBody ManualStrategy strategy);

    // 手动调整照明灯亮度
    @PostMapping(value = "strategy/manual/lamp/brightness", produces = {MediaType.APPLICATION_JSON_VALUE})
    BaseApiResult manualLampBrightnessStrategy(@RequestBody ManualBrightnessStrategy strategy);

    // 下发配电箱策略
    @PostMapping(value = "strategy/box", produces = {MediaType.APPLICATION_JSON_VALUE})
    BaseApiResult newBoxStrategy(@RequestBody BoxStrategy strategy);

    @GetMapping(value = "strategy/box/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BoxStrategy> getBoxStrategies(@PathVariable(name = "organId") String organId);

    // 手动打开关闭配电箱
    @PostMapping(value = "strategy/manual/box", produces = {MediaType.APPLICATION_JSON_VALUE})
    BaseApiResult manualBoxStrategy(@RequestBody ManualStrategy strategy);
}
