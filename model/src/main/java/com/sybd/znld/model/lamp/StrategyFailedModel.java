package com.sybd.znld.model.lamp;

import com.sybd.znld.model.StrategyFailedStatus;

import java.util.UUID;

public class StrategyFailedModel {
    public String id = UUID.randomUUID().toString().replace("-", "");
    public String strategyId;
    public String targetId;
    public Integer count = 0;
    public StrategyFailedStatus status = StrategyFailedStatus.TRYING;
}
