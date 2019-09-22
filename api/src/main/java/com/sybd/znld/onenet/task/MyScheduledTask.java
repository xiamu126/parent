package com.sybd.znld.onenet.task;

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

    //@Scheduled(initialDelay = 2000, fixedDelay = 1000 * 60)
    public void rebootChip(){ // 定时重启中控，每3小时执行一次
        var locker = lockers.get(rebootChip);
        if(locker != null && locker.tryLock()){
            try{
                locker.lock();
                var resource = this.lampService.getResourceByCommandValue(Command.ZNLD_REBOOT_CHIP);
                if(resource == null){
                    log.error("执行定时任务错误，获取重启中控指令为空");
                    return;
                }
                var devices = this.oneNetService.getDeviceIdAndImei();
                for(var device : devices){
                    var db = mongoClient.getDatabase( "test" );
                    var c1 = db.getCollection("com.sybd.znld.task");
                    var filter = new BasicDBObject();
                    var begin = LocalDateTime.now(ZoneOffset.UTC).minusHours(1).minusMinutes(10);
                    filter.put("execute_time", BasicDBObjectBuilder.start("$gt", begin).get());
                    filter.put("deviceId", device.deviceId);
                    if(c1.find(filter).first() != null){ // 如果在过去的3小时10分种内，有执行过任务，则跳过，防止短时间内多次重启
                        log.debug("距离上一次重启，小于1小时，不执行");
                        continue;
                    }else{
                        log.debug("执行重启任务，id为"+device.deviceId);
                    }
                    var params = new CommandParams();
                    params.command = resource.value;
                    params.deviceId = device.deviceId;
                    params.imei = device.imei;
                    params.oneNetKey = resource.toOneNetKey();
                    params.timeout = resource.timeout;
                    var ret = oneNetService.offlineExecute(params);
                    var document = new Document("name", "reboot_chip")
                            .append("deviceId", device.deviceId)
                            .append("execute_time", LocalDateTime.now(ZoneOffset.UTC))
                            .append("execute_result", new Document().append("code", ret.errno).append("msg", ret.error));
                    c1.insertOne(document);
                }
                log.debug("定时重启中控任务执行完毕");
            }catch (Exception ex){
                log.error("执行定时重启中控任务发生错误：" + ex.getMessage());
            }finally {
                locker.forceUnlock();
            }
        }else{
            log.debug("获取锁失败");
        }
    }
}
