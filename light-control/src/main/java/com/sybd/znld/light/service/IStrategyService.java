package com.sybd.znld.light.service;

import com.sybd.znld.light.controller.dto.*;

import java.util.List;

public interface IStrategyService {
    // 下发照明灯策略并保存记录
    boolean newLampStrategy(LampStrategy strategy);
    // 获取某个组织下的所有照明灯策略
    List<LampStrategy> getLampStrategies(String organId);
    // 下发配电箱策略并保存记录
    boolean newBoxStrategy(BoxStrategy strategy);
    // 获取某个组织下的所有配电箱策略
    List<BoxStrategy> getBoxStrategies(String organId);
    // 手动开关照明灯
    boolean sendCmdToLamp(ManualStrategy strategy);
    // 手动开关配电箱
    boolean sendCmdToBox(ManualStrategy strategy);
    //手动调节照明灯亮度
    boolean sendBrightnessCmdToLamp(ManualBrightnessStrategy strategy);
}
