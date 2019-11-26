package com.sybd.znld.model.lamp;

import java.util.UUID;

public class LampStrategyTargetModel {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String targetId;
    public Target targetType;
    public String lampStrategyId;
}
