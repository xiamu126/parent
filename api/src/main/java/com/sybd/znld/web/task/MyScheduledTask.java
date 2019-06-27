package com.sybd.znld.web.task;

import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;

@Slf4j
@Component
public class MyScheduledTask {
    private final ILampService lampService;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;
    private static Map<String, RLock> lockers;
    private static final String heartBeat = "oneNetHeartBeat";

    @PreDestroy
    public void preDestroy(){
        for(var item : lockers.entrySet()){
            var locker = item.getValue();
            locker.forceUnlock();
            log.debug("释放锁："+locker.getName());
        }
    }

    @Autowired
    public MyScheduledTask(ILampService lampService, RedissonClient redissonClient, IOneNetService oneNetService) {
        this.lampService = lampService;
        this.redissonClient = redissonClient;
        this.oneNetService = oneNetService;

        lockers = Map.of(heartBeat, this.redissonClient.getLock(heartBeat));
    }

    //@Scheduled(initialDelay = 2000, fixedDelay = 1000*5)
    public void oneNetHeartBeat(){
        var locker = lockers.get(heartBeat);
        if(locker.tryLock()){
            try{
                locker.lock();
                //log.debug("成功获取锁并开始执行任务");
                var model = this.lampService.getResourceByCommandValue(Command.ZNLD_HEART_BEAT);
                if(model == null){
                    //log.error("执行定时任务错误，获取指令为空");
                    return;
                }
                var map = this.oneNetService.getDeviceIdAndImei();
                for(var item: map){
                    var params = new CommandParams();
                    params.command = Command.ZNLD_HEART_BEAT;
                    params.deviceId = item.deviceId;
                    params.imei = item.imei;
                    params.oneNetKey = model.toOneNetKey();
                    params.timeout = model.timeout;
                    oneNetService.execute(params);
                }
            }finally {
                locker.forceUnlock();
                //log.debug("执行任务完毕，并释放锁");
            }
        }else{
            log.debug("获取锁失败");
        }
    }
}
