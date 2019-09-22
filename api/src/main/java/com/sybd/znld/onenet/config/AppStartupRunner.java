package com.sybd.znld.onenet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
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

    @Autowired
    public AppStartupRunner(CacheManager cacheManager,
                            TaskScheduler taskScheduler,
                            ProjectConfig projectConfig,
                            RedissonClient redissonClient,
                            ObjectMapper objectMapper) {
        this.baseService = new BaseService(cacheManager, taskScheduler, projectConfig, redissonClient, objectMapper);
    }

    @Override
    public void run(ApplicationArguments args){
        log.debug("执行程序启动初始化任务");
        this.baseService.removeAllCache();
    }
}
