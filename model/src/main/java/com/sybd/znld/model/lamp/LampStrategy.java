package com.sybd.znld.model.lamp;

import com.sybd.znld.model.Status;

import java.time.LocalDate;
import java.util.UUID;

public class LampStrategy {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String name;
    public LocalDate fromDate;
    public LocalDate toDate;
    public boolean autoGenerateTime;
    public Integer type;
    public Status status;
}
