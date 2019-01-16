package com.sybd.znld.video;

import com.sybd.znld.config.RedisKeyConfig;
import com.sybd.znld.video.dto.VideoData;
import com.sybd.znld.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VideoTask {
    private final RedisService redisService;
    private final VideoRepository videoRepository;

    @Autowired
    public VideoTask(RedisService redisService, VideoRepository videoRepository) {
        this.redisService = redisService;
        this.videoRepository = videoRepository;
    }

    public boolean push(String key, VideoData input) {
        videoRepository.push(input);
        redisService.set(key, "", 30, TimeUnit.SECONDS);
        var persistentKey = getPersistentKey(key);
        redisService.set(persistentKey, "");
        return true;
    }

    private String getPersistentKey(String redisKey){
        var channel = getChannel(redisKey);
        return RedisKeyConfig.CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX+channel;//client:channelGuid:persistence:2
    }

    private String getChannel(String redisKey){
        var index = redisKey.lastIndexOf(":"); //client:channelGuid:2
        return redisKey.substring(index+1);
    }

    public boolean stop(String key, VideoData input){
        var channel = getChannel(key);
        videoRepository.stop(channel);
        redisService.delete(key);
        return true;
    }

    private String getRedisKey(String redisPersistenceKey){//client:channelGuid:persistence:2
        var channel = getChannel(redisPersistenceKey);
        return RedisKeyConfig.CLIENT_CHANNEL_GUID_PREFIX+channel;//client:channelGuid:2
    }

    public void verify(){
        var persistentKeys = redisService.scan(RedisKeyConfig.CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX_MATCH);//client:channelGuid:persistence:*
        for(var key: persistentKeys){//client:channelGuid:persistence:2
            var redisKey = getRedisKey(key.toString());
            if(this.redisService.exists(redisKey)) {
                log.debug("当前key有效："+ redisKey+",不关闭");
            }else{
                var channel = getChannel(redisKey);
                //log.debug("当前key无效："+ redisKey+"，关闭channel："+channel);
                videoRepository.stop(channel);
            }
        }
    }

    public BufferedImage pickImage(String channelGuid){
        return this.videoRepository.pickImage(channelGuid);
    }
}
