package com.sybd.znld.model.lamp;

import com.sybd.znld.model.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class LampStrategyModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String name;
    public LocalDate fromDate;
    public LocalDate toDate;
    public LocalTime fromTime;
    public LocalTime toTime;
    public boolean autoGenerateTime;
    public Strategy type;
    public Status status = Status.LAMP_STRATEGY_READY;
}
