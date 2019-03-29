package com.sybd.znld.task;

import com.sybd.znld.onenet.IOneNetService;
import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.onenet.dto.CommandParams;
import com.sybd.znld.service.znld.IExecuteCommandService;
import com.sybd.znld.service.znld.IVideoService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;

@Component
public class MyScheduledTask {
    private final IExecuteCommandService executeCommandService;
    private final IVideoService videoService;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;
    private static Map<String, RLock> lockers;
    private static final String heartBeat = "oneNetHeartBeat";

    private final Logger log = LoggerFactory.getLogger(MyScheduledTask.class);

    @PreDestroy
    public void preDestroy(){
        for(var item : lockers.entrySet()){
            var locker = item.getValue();
            locker.forceUnlock();
            log.debug("释放锁："+locker.getName());
        }
    }

    @Autowired
    public MyScheduledTask(IExecuteCommandService executeCommandService, IVideoService videoService, RedissonClient redissonClient, IOneNetService oneNetService) {
        this.executeCommandService = executeCommandService;
        this.videoService = videoService;
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
                var model = this.executeCommandService.getParamsByCommand(OneNetService.ZNLD_HEART_BEAT);
                if(model == null){
                    //log.error("执行定时任务错误，获取指令为空");
                    return;
                }
                var map = this.oneNetService.getDeviceIdAndIMEI();
                for(var item: map.entrySet()){
                    var params = new CommandParams();
                    params.command = OneNetService.ZNLD_HEART_BEAT;
                    params.deviceId = item.getKey();
                    params.imei = item.getValue();
                    params.objId = model.oneNetKey.objId;
                    params.objInstId = model.oneNetKey.objInstId;
                    params.resId = model.oneNetKey.resId;
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

    //@Scheduled(initialDelay = 1000, fixedDelay = 1000*10)
    public void videoCheck(){
        this.videoService.verify();
    }
}
