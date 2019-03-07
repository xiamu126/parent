package com.sybd.znld.service.impl;

import com.sybd.znld.config.RedisKeyConfig;
import com.sybd.znld.service.VideoService;
import com.sybd.znld.video.VideoRepository;
import com.sybd.znld.video.dto.VideoData;
import com.sybd.znld.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoServiceImpl implements VideoService {
    private final RedisService redisService;
    private final VideoRepository videoRepository;

    private final Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    public VideoServiceImpl(RedisService redisService, VideoRepository videoRepository) {
        this.redisService = redisService;
        this.videoRepository = videoRepository;
    }

    @Override
    public boolean push(String key, VideoData input) {
        videoRepository.push(input);
        redisService.set(key, "", 30, TimeUnit.SECONDS);
        String persistentKey = getPersistentKey(key);
        redisService.set(persistentKey, "");
        return true;
    }

    private String getPersistentKey(String redisKey){
        String channel = getChannel(redisKey);
        return RedisKeyConfig.CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX+channel;//client:channelGuid:persistence:2
    }

    private String getChannel(String redisKey){
        int index = redisKey.lastIndexOf(":"); //client:channelGuid:2
        return redisKey.substring(index+1);
    }

    @Override
    public boolean stop(String key, VideoData input){
        String channel = getChannel(key);
        videoRepository.stop(channel);
        redisService.delete(key);
        return true;
    }

    private String getRedisKey(String redisPersistenceKey){//client:channelGuid:persistence:2
        String channel = getChannel(redisPersistenceKey);
        return RedisKeyConfig.CLIENT_CHANNEL_GUID_PREFIX+channel;//client:channelGuid:2
    }

    @Override
    public void verify(){
        List<String> persistentKeys = redisService.scan(RedisKeyConfig.CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX_MATCH);//client:channelGuid:persistence:*
        for(var key: persistentKeys){//client:channelGuid:persistence:2
            var redisKey = getRedisKey(key);
            if(this.redisService.exists(redisKey)) {
                log.debug("当前key有效："+ redisKey+",不关闭");
            }else{
                var channel = getChannel(redisKey);
                log.debug("当前key无效："+ redisKey+"，关闭channel："+channel);
                videoRepository.stop(channel);
            }
        }
    }

    @Override
    public void stopAll(){
        this.videoRepository.stopAll();
    }

    @Override
    public boolean isChannelInUsing(String channelGuid) {
        return this.videoRepository.isChannelInUsing(channelGuid);
    }

    @Override
    public BufferedImage pickImage(String channelGuid){
        return this.videoRepository.pickImage(channelGuid);
    }
}
