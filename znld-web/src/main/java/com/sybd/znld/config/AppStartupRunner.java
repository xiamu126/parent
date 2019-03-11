package com.sybd.znld.config;

import com.sybd.znld.service.VideoService;
import com.sybd.znld.service.impl.BaseServiceImpl;
import com.sybd.znld.service.v2.oauth.OAuthService;
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
    private final BaseServiceImpl baseService;
    private final Logger log = LoggerFactory.getLogger(AppStartupRunner.class);

    private final VideoService videoService;

    @Autowired
    public AppStartupRunner(CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig, VideoService videoService) {
        this.baseService = new BaseServiceImpl(cacheManager, taskScheduler, projectConfig);
        this.videoService = videoService;
    }

    @Override
    public void run(ApplicationArguments args){
        log.debug("执行程序启动初始化任务");
        this.baseService.removeAllCache();
        this.videoService.stopAll();
    }
}
