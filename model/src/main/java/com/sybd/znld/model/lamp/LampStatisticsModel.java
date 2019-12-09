package com.sybd.znld.model.lamp;

import java.time.LocalDateTime;
import java.util.UUID;

public class LampStatisticsModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public Boolean online;
    public Boolean fault;
    public Boolean light;
    public Integer electricity;
    public LocalDateTime updateTime;
}
