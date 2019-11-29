package com.sybd.znld.model.lamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.Status;

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
    public Status status = Status.LAMP_STRATEGY_READY;
}
