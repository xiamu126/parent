package com.sybd.znld.config;

import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.znld.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppStartupRunner implements ApplicationRunner {
    private final BaseService baseService;
    private final IVideoService videoService;

    @Autowired
    public AppStartupRunner(CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig, IVideoService videoService) {
        this.baseService = new BaseService(cacheManager, taskScheduler, projectConfig);
        this.videoService = videoService;
    }

    @Override
    public void run(ApplicationArguments args){
        log.debug("执行程序启动初始化任务");
        this.baseService.removeAllCache();
        this.videoService.stopAll();
    }
}
