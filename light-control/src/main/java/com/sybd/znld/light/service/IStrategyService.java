package com.sybd.znld.light.service;

import com.sybd.znld.model.*;
import com.sybd.znld.model.lamp.LampStrategyModel;
import com.sybd.znld.model.lamp.dto.LampManualCmd;
import com.sybd.znld.model.lamp.dto.LampStrategy;
import com.sybd.znld.model.lamp.dto.LampStrategyCmd;
import com.sybd.znld.model.lamp.dto.LampStrategyOutput;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

public interface IStrategyService {
    // 查看当前策略是否会覆盖某个已经在执行中的策略，新增的策略必须在时间上、作用对象上完全包含已经存在的
    Boolean isLampStrategyOverlapping(LampStrategy strategy);
    // 终止某个待执行的策略
    void terminateStrategy(String id);
    // 新建照明灯策略
    BaseApiResult newLampStrategy(LampStrategy strategy);
    // 删除照明灯策略
    BaseApiResult deleteLampStrategy(String strategyId);
    // 针对目标对象，执行指定的策略
    Map<String, BaseApiResult> executeLampStrategy(LampStrategyCmd cmd);
    // 针对目标对象，只是实时控制
    Map<String, BaseApiResult> executeLampStrategy(LampManualCmd cmd);
    // 获取某个组织下的所有照明灯策略
    List<LampStrategyOutput> getLampStrategies(String organId);
    // 获取某个组织下的特定状态的所有照明灯策略
    List<LampStrategyOutput> getLampStrategies(String organId, LampStrategyModel.Status status);
    // 定期检查待处理的策略
    void processWaitingStrategies();
    // 定期检查发送失败的
    void processFailedLamps();
}
