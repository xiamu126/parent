package com.sybd.znld.light.job;

import com.sybd.znld.light.service.IStrategyService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@JobHandler(value="processFailedLamps")
@Component
public class ProcessFailedLamps extends IJobHandler {
    private final IStrategyService strategyService;

    @Autowired
    public ProcessFailedLamps(IStrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @Override
    public ReturnT<String> execute(String param){
        XxlJobLogger.log("开始策略重试");
        this.strategyService.processFailedLamps();
        return SUCCESS;
    }
}
