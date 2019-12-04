package com.sybd.znld.web.config;

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

    @Autowired
    public AppStartupRunner() {
    }

    @Override
    public void run(ApplicationArguments args){
        log.debug("执行程序启动初始化任务");
    }
}
