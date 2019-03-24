package com.sybd.znld.task;

import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.onenet.dto.CommandParams;
import com.sybd.znld.onenet.OneNetConfig;
import com.sybd.znld.service.OneNetConfigDeviceService;
import com.sybd.znld.service.VideoService;
import com.sybd.znld.service.znld.IExecuteCommandService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;

@Component
public class MyScheduledTask {
    private final OneNetService oneNet;
    private final IExecuteCommandService executeCommandService;
    private final VideoService videoService;
    private final RedissonClient redissonClient;
    private final OneNetConfigDeviceService oneNetConfigDeviceService;
    private static Map<String, RLock> lockers;
    private static final String heartBeat = "oneNetHeartBeat";

    private final Logger log = LoggerFactory.getLogger(MyScheduledTask.class);

    @PreDestroy
    public void preDestroy(){
        for(Map.Entry<String, RLock> item : lockers.entrySet()){
            RLock locker = item.getValue();
            locker.forceUnlock();
            log.debug("释放锁："+locker.getName());
        }
    }

    @Autowired
    public MyScheduledTask(OneNetService oneNet,
                           IExecuteCommandService executeCommandService,
                           VideoService videoService,
                           RedissonClient redissonClient,
                           OneNetConfigDeviceService oneNetConfigDeviceService) {
        this.oneNet = oneNet;
        this.executeCommandService = executeCommandService;
        this.videoService = videoService;
        this.redissonClient = redissonClient;
        this.oneNetConfigDeviceService = oneNetConfigDeviceService;

        lockers = Map.of(heartBeat, this.redissonClient.getLock(heartBeat));
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 1000*5)
    public void oneNetHeartBeat(){
        var locker = lockers.get(heartBeat);
        if(locker.tryLock()){
            try{
                locker.lock();
                //log.debug("成功获取锁并开始执行任务");
                var entity = this.executeCommandService.getParamsByCommand(OneNetConfig.ExecuteCommand.ZNLD_HEART_BEAT.getValue(), false);
                if(entity == null){
                    //log.error("执行定时任务错误，获取指令为空");
                    return;
                }
                Map<Integer, String> map = this.oneNetConfigDeviceService.getDeviceIdAndImeis();
                for(Map.Entry<Integer, String> item : map.entrySet()){
                    var params = new CommandParams(item.getKey(),item.getValue(), entity.getOneNetKey(), entity.getTimeout(), OneNetConfig.ExecuteCommand.ZNLD_HEART_BEAT.getValue());
                    oneNet.execute(params);
                }
            }finally {
                locker.forceUnlock();
                //log.debug("执行任务完毕，并释放锁");
            }
        }else{
            log.debug("获取锁失败");
        }
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000*10)
    public void videoCheck(){
        this.videoService.verify();
    }
}
