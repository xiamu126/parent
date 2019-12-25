package com.sybd.znld.onenet.config;

import com.sybd.znld.onenet.service.IMessageService;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;


@Slf4j
public class MyScheduledTask {
    private final ILampService lampService;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;
    private final IMessageService messageService;

    public MyScheduledTask(ILampService lampService,
                           RedissonClient redissonClient,
                           IOneNetService oneNetService,
                           IMessageService messageService) {
        this.lampService = lampService;
        this.redissonClient = redissonClient;
        this.oneNetService = oneNetService;
        this.messageService = messageService;
    }

    @Async("TaskThreadPool")
    @Scheduled(cron = "${schedule.cron}")
    public void doStatistics() {
        var lock = this.redissonClient.getLock(this.getClass().getName());
        if(lock.tryLock()) {
            try {
                lock.lock();
                log.debug("定时任务");
                this.messageService.scheduledStatistics();
            }finally {
                lock.forceUnlock();
            }
        }else {
            log.debug("获取锁失败");
        }
    }
}
