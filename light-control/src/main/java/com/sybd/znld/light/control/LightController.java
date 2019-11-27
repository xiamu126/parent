package com.sybd.znld.light.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.light.control.dto.ManualBrightnessStrategy;
import com.sybd.znld.light.control.dto.ManualStrategy;
import com.sybd.znld.light.control.dto.NewBoxStrategy;
import com.sybd.znld.light.control.dto.NewLampStrategy;
import com.sybd.znld.util.MyDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;

@RestController
@RequestMapping("/api/v1/light")
public class LightController implements ILightController {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public String newLampStrategy(NewLampStrategy strategy) {
        var from = MyDateTime.toLocalDateTime(strategy.from, ZoneId.of("Asia/Shanghai"));
        var to = MyDateTime.toLocalDateTime(strategy.to, ZoneId.of("Asia/Shanghai"));
        var fromDate = from.toLocalDate();
        var fromTime = from.toLocalTime();
        var toDate = to.toLocalDate();
        var toTime = to.toLocalTime();
        try {
            return this.objectMapper.writeValueAsString(strategy);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String manualLampStrategy(ManualStrategy strategy) {
        try {
            return this.objectMapper.writeValueAsString(strategy);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String manualLampBrightnessStrategy(ManualBrightnessStrategy strategy) {
        try {
            return this.objectMapper.writeValueAsString(strategy);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String newBoxStrategy(NewBoxStrategy strategy) {
        var from = MyDateTime.toLocalDateTime(strategy.from, ZoneId.of("Asia/Shanghai"));
        var to = MyDateTime.toLocalDateTime(strategy.to, ZoneId.of("Asia/Shanghai"));
        var fromDate = from.toLocalDate();
        var fromTime = from.toLocalTime();
        var toDate = to.toLocalDate();
        var toTime = to.toLocalTime();
        try {
            return this.objectMapper.writeValueAsString(strategy);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    @Override
    public String manualBoxStrategy(ManualStrategy strategy) {
        try {
            return this.objectMapper.writeValueAsString(strategy);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }
}
