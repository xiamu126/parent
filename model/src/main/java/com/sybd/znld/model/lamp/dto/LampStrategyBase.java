package com.sybd.znld.model.lamp.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class LampStrategyBase {
    public String strategyId;
    public String strategyName;
    public LocalDate fromDate;
    public LocalDate toDate;
    public LocalTime fromTime;
    public LocalTime toTime;
    public String targetId;
}
