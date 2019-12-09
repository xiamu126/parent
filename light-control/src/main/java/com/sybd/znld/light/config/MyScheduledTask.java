package com.sybd.znld.light.config;

import com.sybd.znld.light.service.IStrategyService;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class MyScheduledTask {
    private final ILampService lampService;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;
    private final IStrategyService strategyService;

    @Autowired
    public MyScheduledTask(ILampService lampService, RedissonClient redissonClient, IOneNetService oneNetService, IStrategyService strategyService) {
        this.lampService = lampService;
        this.redissonClient = redissonClient;
        this.oneNetService = oneNetService;
        this.strategyService = strategyService;
    }

    @Async("TaskThreadPool")
    @Scheduled(fixedDelay = 3000)
    public void test() {
        var lock = this.redissonClient.getLock(this.getClass().getName());
        if(lock.tryLock()) {
            try {
                lock.lock();
                this.strategyService.processPendingStrategies();
                this.strategyService.processFailedLamps();
            }finally {
                lock.forceUnlock();
            }
        }else {
            log.debug("获取锁失败");
        }
    }
}
