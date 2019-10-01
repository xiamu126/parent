package com.sybd.znld.environment.controller.dto;

import com.sybd.znld.model.lamp.dto.LampSummary;

import java.util.List;

public class LampSummaryResult {
    public Integer totalLampCount;
    public Integer totalCityCount;
    public List<LampSummary> detail;
}
