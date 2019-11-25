package com.sybd.znld.model.lamp;

import java.util.UUID;

public class LampStrategyTarget {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String targetId;
    public Integer targetType;
    public String lampStrategyId;
}
