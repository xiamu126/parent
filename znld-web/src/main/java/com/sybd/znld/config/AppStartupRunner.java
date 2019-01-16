package com.sybd.znld.config;

import com.sybd.znld.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppStartupRunner implements ApplicationRunner {
    private final BaseServiceImpl baseService;

    @Autowired
    public AppStartupRunner(CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        this.baseService = new BaseServiceImpl(cacheManager, taskScheduler, projectConfig);
    }

    @Override
    public void run(ApplicationArguments args){
        log.debug("执行程序启动初始化任务");
        this.baseService.removeAllCache();
    }
}
