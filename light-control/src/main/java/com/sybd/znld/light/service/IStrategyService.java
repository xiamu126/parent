package com.sybd.znld.light.service;

import com.sybd.znld.light.control.dto.BaseStrategy;
import com.sybd.znld.light.control.dto.LampStrategy;

import java.util.List;

public interface IStrategyService {
    // 下发照明灯策略并保存记录
    boolean newLampStrategy(LampStrategy strategy);
    // 获取某个组织下的所有照明灯策略
    List<LampStrategy> getLampStrategies(String organId);
    // 下发配电箱策略并保存记录
    boolean newBoxStrategy(BaseStrategy strategy);
    // 获取某个组织下的所有配电箱策略
    List<BaseStrategy> getBoxStrategies(String organId);
}
