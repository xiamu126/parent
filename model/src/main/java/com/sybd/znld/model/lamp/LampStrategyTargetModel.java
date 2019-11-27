package com.sybd.znld.model.lamp;

import java.util.UUID;

public class LampStrategyTargetModel {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String targetId; // 这个id可以是路灯或者配电箱
    public String lampStrategyId;
}
