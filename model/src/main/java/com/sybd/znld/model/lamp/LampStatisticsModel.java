package com.sybd.znld.model.lamp;

import java.time.LocalDateTime;
import java.util.UUID;

public class LampStatisticsModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String lampId;
    public String regionId;
    public String organId;
    public Boolean isOnline;
    public Boolean isFault;
    public Boolean isLight;
    public Double energy;
    public LocalDateTime updateTime;
}
