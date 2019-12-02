package com.sybd.znld.web.task;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClient;
import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.*;
import java.util.Map;

@Slf4j
@Component
public class MyScheduledTask {
    private final ILampService lampService;
    private final RedissonClient redissonClient;
    private final IOneNetService oneNetService;
    private static Map<String, RLock> lockers;
    private static final String heartBeat = "oneNetHeartBeat";
    private static final String rebootChip = "oneNetRebootChip";
    private final MongoClient mongoClient;

    @PreDestroy
    public void preDestroy(){
        for(var item : lockers.entrySet()){
            var locker = item.getValue();
            locker.forceUnlock();
            log.debug("释放锁："+locker.getName());
        }
    }

    @Autowired
    public MyScheduledTask(ILampService lampService, RedissonClient redissonClient, IOneNetService oneNetService, MongoClient mongoClient) {
        this.lampService = lampService;
        this.redissonClient = redissonClient;
        this.oneNetService = oneNetService;
        this.mongoClient = mongoClient;

        lockers = Map.of(heartBeat, this.redissonClient.getLock(heartBeat),rebootChip, this.redissonClient.getLock(rebootChip));
    }
}
