package com.sybd.znld.light.job;

import com.sybd.znld.light.service.IStrategyService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@JobHandler(value="processWaitingStrategies")
@Component
public class ProcessWaitingStrategies extends IJobHandler {

	private final IStrategyService strategyService;

	@Autowired
	public ProcessWaitingStrategies(IStrategyService strategyService) {
		this.strategyService = strategyService;
	}

	@Override
	public ReturnT<String> execute(String param){
		XxlJobLogger.log("开始处理等待中的策略");
		this.strategyService.processWaitingStrategies();
		return SUCCESS;
	}
}
