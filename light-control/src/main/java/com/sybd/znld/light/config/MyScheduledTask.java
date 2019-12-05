package com.sybd.znld.light.config;

import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;

@Slf4j
@Configuration
@EnableScheduling
public class MyScheduledTask {
    private final ILampService lampService;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;

    @Autowired
    public MyScheduledTask(ILampService lampService, RedissonClient redissonClient, IOneNetService oneNetService) {
        this.lampService = lampService;
        this.redissonClient = redissonClient;
        this.oneNetService = oneNetService;
    }

    @Async("TaskThreadPool")
    @Scheduled(cron = "0 0 * * * ?")
    public void test() {
        var lock = this.redissonClient.getLock(this.getClass().getName());
        if(lock.tryLock()) {
            try {
                lock.lock();
                log.debug("每天8点47定时任务");
            }finally {
                lock.forceUnlock();
            }
        }else {
            log.debug("获取锁失败");
        }
    }
}
