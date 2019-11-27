package com.sybd.znld.light.service;

import com.sybd.znld.light.control.dto.NewBoxStrategy;
import com.sybd.znld.light.control.dto.NewLampStrategy;

public interface IStrategyService {
    // 下发照明灯策略并保存记录
    boolean newLampStrategy(NewLampStrategy strategy);
    // 下发配电箱策略并保存记录
    boolean newBoxStrategy(NewBoxStrategy strategy);
}
