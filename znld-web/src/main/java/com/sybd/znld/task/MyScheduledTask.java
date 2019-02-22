package com.sybd.znld.task;

import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.onenet.dto.CommandParams;
import com.sybd.znld.onenet.OneNetConfig;
import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.service.ExecuteCommandService;
import com.sybd.znld.service.OneNetConfigDeviceService;
import com.sybd.znld.video.VideoTask;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class MyScheduledTask {
    private final OneNetService oneNet;
    private final ExecuteCommandService executeCommandService;
    private final VideoTask videoTask;
    private final RedissonClient redissonClient;
    private final OneNetConfigDeviceService oneNetConfigDeviceService;
    private static Map<String, RLock> lockers;
    private static final String heartBeat = "oneNetHeartBeat";

    private Logger log = LoggerFactory.getLogger(MyScheduledTask.class);

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
                           ExecuteCommandService executeCommandService,
                           VideoTask videoTask,
                           RedissonClient redissonClient,
                           OneNetConfigDeviceService oneNetConfigDeviceService) {
        this.oneNet = oneNet;
        this.executeCommandService = executeCommandService;
        this.videoTask = videoTask;
        this.redissonClient = redissonClient;
        this.oneNetConfigDeviceService = oneNetConfigDeviceService;

        HashMap<String, RLock> map = new HashMap<>();
        map.put(heartBeat, this.redissonClient.getLock(heartBeat));
        lockers = Collections.unmodifiableMap(map);
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 1000*5)
    public void oneNetHeartBeat(){
        RLock locker = lockers.get(heartBeat);
        if(locker.tryLock()){
            try{
                locker.lock();
                //log.debug("成功获取锁并开始执行任务");
                OneNetExecuteParams entity = this.executeCommandService.getParamsByCommand(OneNetConfig.ExecuteCommand.ZNLD_HEART_BEAT.getValue(), false);
                if(entity == null){
                    //log.error("执行定时任务错误，获取指令为空");
                    return;
                }
                Map<Integer, String> map = this.oneNetConfigDeviceService.getDeviceIdAndImeis();
                for(Map.Entry<Integer, String> item : map.entrySet()){
                    CommandParams params = new CommandParams(item.getKey(),item.getValue(), entity.getOneNetKey(), entity.getTimeout(), OneNetConfig.ExecuteCommand.ZNLD_HEART_BEAT.getValue());
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

    @Scheduled(initialDelay = 1000, fixedDelay = 1000*5)
    public void videoCheck(){
        this.videoTask.verify();
    }
}
