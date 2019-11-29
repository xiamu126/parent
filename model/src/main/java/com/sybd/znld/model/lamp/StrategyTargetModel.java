package com.sybd.znld.model.lamp;

import java.util.UUID;

public class StrategyTargetModel {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String targetId; // 这个id可以是路灯或者配电箱
    public Target targetType;
    public String strategyId;
}
