package com.sybd.znld.config;

import com.sybd.znld.service.video.IVideoService;
import com.sybd.znld.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private final BaseService baseService;
    private final Logger log = LoggerFactory.getLogger(AppStartupRunner.class);

    private final IVideoService IVideoService;

    @Autowired
    public AppStartupRunner(CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig, IVideoService IVideoService) {
        this.baseService = new BaseService(cacheManager, taskScheduler, projectConfig);
        this.IVideoService = IVideoService;
    }

    @Override
    public void run(ApplicationArguments args){
        log.debug("执行程序启动初始化任务");
        this.baseService.removeAllCache();
        this.IVideoService.stopAll();
    }
}
