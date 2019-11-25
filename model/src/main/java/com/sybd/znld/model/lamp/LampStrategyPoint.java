package com.sybd.znld.model.lamp;

import java.time.LocalTime;
import java.util.UUID;

public class LampStrategyPoint {
    public String id = UUID.randomUUID().toString().replace("-","");
    public LocalTime at;
    public Integer brightness;
    public String lampStrategyId;
}
