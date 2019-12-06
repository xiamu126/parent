package com.sybd.znld.light.service;

import com.sybd.znld.light.controller.dto.*;
import com.sybd.znld.model.*;
import com.sybd.znld.model.lamp.Target;
import com.sybd.znld.model.onenet.dto.BaseResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IStrategyService {
    // 终止某个待执行的策略
    void terminateStrategy(String id);
    // 下发照明灯策略并保存记录
    ApiResult newLampStrategy(LampStrategy strategy);
    // 获取某个组织下的所有照明灯策略
    List<LampStrategy> getLampStrategies(String organId);
    // 获取某个组织下的特定状态的所有照明灯策略
    List<LampStrategy> getLampStrategies(String organId, StrategyStatus status);
    // 获取某个组织下的特定状态并指定时间区间的所有照明灯策略
    List<LampStrategy> getLampStrategies(String organId, StrategyStatus status, LocalDate begin, LocalDate end);
    // 下发配电箱策略并保存记录
    Map<Target, ArrayList<Pair<String, BaseResult>>> newBoxStrategy(BoxStrategy strategy);
    // 获取某个组织下的所有配电箱策略
    List<BoxStrategy> getBoxStrategies(String organId);
    // 手动开关照明灯
    Map<Target, ArrayList<Pair<String, BaseResult>>> newLampManual(ManualStrategy strategy);
    // 手动开关配电箱
    Map<Target, ArrayList<Pair<String, BaseResult>>> newBoxManual(ManualStrategy strategy);
    //手动调节照明灯亮度
    Map<Target, ArrayList<Pair<String, BaseResult>>> newLampManualBrightness(ManualStrategy strategy);

    // 定期检查待处理的策略
    void processPendingStrategies(String organId);
    void processPendingStrategies();

    // 定期检查发送失败的
    void processFailedLamps(String organId);
}
