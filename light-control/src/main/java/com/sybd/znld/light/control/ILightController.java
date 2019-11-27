package com.sybd.znld.light.control;

import com.sybd.znld.light.control.dto.ManualBrightnessStrategy;
import com.sybd.znld.light.control.dto.ManualStrategy;
import com.sybd.znld.light.control.dto.NewBoxStrategy;
import com.sybd.znld.light.control.dto.NewLampStrategy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ILightController {
    // 下发照明灯策略
    @PostMapping(value = "lamp/strategy", produces = {MediaType.APPLICATION_JSON_VALUE})
    String newLampStrategy(@RequestBody NewLampStrategy strategy);

    // 手动打开关闭照明灯
    @PostMapping(value = "lamp/manual/strategy", produces = {MediaType.APPLICATION_JSON_VALUE})
    String manualLampStrategy(@RequestBody ManualStrategy strategy);

    // 手动调整照明灯亮度
    @PostMapping(value = "lamp/manual/brightness/strategy", produces = {MediaType.APPLICATION_JSON_VALUE})
    String manualLampBrightnessStrategy(@RequestBody ManualBrightnessStrategy strategy);

    // 下发配电箱策略
    @PostMapping(value = "box/strategy", produces = {MediaType.APPLICATION_JSON_VALUE})
    String newBoxStrategy(@RequestBody NewBoxStrategy strategy);

    // 手动打开关闭配电箱
    @PostMapping(value = "box/manual/strategy", produces = {MediaType.APPLICATION_JSON_VALUE})
    String manualBoxStrategy(@RequestBody ManualStrategy strategy);
}
