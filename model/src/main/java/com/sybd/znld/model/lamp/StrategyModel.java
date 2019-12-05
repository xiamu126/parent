package com.sybd.znld.model.lamp;

import com.sybd.znld.model.StrategyStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class StrategyModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String name;
    public LocalDate fromDate;
    public LocalDate toDate;
    public LocalTime fromTime;
    public LocalTime toTime;
    public boolean autoGenerateTime;
    public Strategy type;
    public String organizationId;
    public String userId;
    public StrategyStatus status = StrategyStatus.READY;
}
