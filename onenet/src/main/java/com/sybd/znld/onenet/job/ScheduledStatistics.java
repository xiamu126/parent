package com.sybd.znld.onenet.job;

import com.sybd.znld.onenet.service.IMessageService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@JobHandler(value="scheduledStatistics")
@Component
public class ScheduledStatistics extends IJobHandler {
    private final IMessageService messageService;

    @Autowired
    public ScheduledStatistics(IMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public ReturnT<String> execute(String param){
        XxlJobLogger.log("开始数据统计");
        this.messageService.scheduledStatistics();
        return SUCCESS;
    }
}
