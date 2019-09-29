package com.sybd.znld.environment.controller;

import com.sybd.znld.environment.controller.dto.LampSummaryResult;
import com.sybd.znld.mapper.lamp.LampMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "其它接口")
@RestController
@RequestMapping("/api/v1/environment/other")
public class OtherController {
    private final LampMapper lampMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public OtherController(LampMapper lampMapper) {
        this.lampMapper = lampMapper;
    }

    @GetMapping(value="lamp/summary", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public LampSummaryResult getLampSummary(){
        var tmp = this.lampMapper.selectLampSummary();
        if(tmp == null || tmp.isEmpty()) return null;
        var totalLampCount = tmp.stream().mapToInt(l -> l.lampCount).sum();
        var result = new LampSummaryResult();
        result.totalLampCount = totalLampCount;
        result.totalCityCount = tmp.size();
        result.detail = tmp;
        return result;
    }
}
