package com.sybd.znld.service.znld;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.config.RedisKeyConfig;
import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.VideoConfigModel;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.video.VideoAsyncTask;
import com.sybd.znld.service.video.dto.VideoData;
import com.sybd.znld.service.znld.mapper.VideoConfigMapper;
import com.whatever.util.MyString;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@DbSource("znld")
public class VideoService extends BaseService implements IVideoService {
    private final RedissonClient redissonClient;
    private final Logger log = LoggerFactory.getLogger(VideoService.class);
    private final VideoConfigMapper videoConfigMapper;
    private final VideoAsyncTask videoAsyncTask;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public VideoService(VideoConfigMapper videoConfigMapper,
                        CacheManager cacheManager,
                        TaskScheduler taskScheduler, ProjectConfig projectConfig,
                        RedissonClient redissonClient,
                        VideoAsyncTask videoAsyncTask) {
        super(cacheManager, taskScheduler, projectConfig);
        this.videoConfigMapper = videoConfigMapper;
        this.redissonClient = redissonClient;
        this.videoAsyncTask = videoAsyncTask;
    }

    @PreDestroy
    public void preDestroy(){
        log.debug("VideoService关闭所有正在执行的任务");
        this.stopAll();
    }

    @Override
    public VideoConfigModel getConfigByCameraId(String cameraId) {
        if(!MyString.isUuid(cameraId)) return null;
        return this.videoConfigMapper.getConfigByCameraId(cameraId);
    }

    @Override
    public VideoConfigModel setConfigByCameraId(VideoConfigModel model) {
        if(model == null || !MyString.isUuid(model.id)) return null;
        if(videoConfigMapper.setConfigByCameraId(model) > 0) return model;
        return null;
    }

    @Override
    public boolean push(String channelGuid) {
        var videoConfig = videoConfigMapper.getConfigByCameraId(channelGuid);
        if(videoConfig == null){
            log.error("获取摄像头配置为空，"+channelGuid);
            return false;
        }
        var rtspUrl = videoConfig.getRtspUrl();
        var rtmpUrl = videoConfig.getRtmpUrl();
        var recordAudio = videoConfig.getRecordAudio();
        this.videoAsyncTask.push(channelGuid, rtspUrl, rtmpUrl, recordAudio ? 1 : 0);
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
    public void stop(String channelGuid){
        this.videoAsyncTask.stop(channelGuid);
    }

    private String getRedisKey(String redisPersistenceKey){//client:channelGuid:persistence:2
        var channel = getChannel(redisPersistenceKey);
        return RedisKeyConfig.CLIENT_CHANNEL_GUID_PREFIX+channel;//client:channelGuid:2
    }

    @Override
    public void verify(){//这个函数已经没有必要了，因为globalKey会自动过期
        var persistentKeys = redissonClient.getKeys().getKeysByPattern(RedisKeyConfig.CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX_MATCH);//client:channelGuid:persistence:*
        for(var key: persistentKeys){//client:channelGuid:persistence:2
            var redisKey = getRedisKey(key);
            if(this.redissonClient.getKeys().countExists(redisKey) > 0) {
                log.debug("当前key有效："+ redisKey+",不关闭");
            }else{
                var channelGuid = getChannel(redisKey);
                log.debug("当前key无效："+ redisKey+"，关闭channel："+channelGuid);
                this.stop(channelGuid);
            }
        }
    }

    @Override
    public void stopAll(){
        this.videoAsyncTask.stopAll();
    }

    @Override
    public boolean isChannelInUsing(String channelGuid) {
        return this.videoAsyncTask.isChannelInUsing(channelGuid);
    }

    @Override
    public BufferedImage pickImage(String channelGuid){
        var videoConfig = videoConfigMapper.getConfigByCameraId(channelGuid);
        if(videoConfig == null){
            log.error("获取摄像头配置为空，"+channelGuid);
            return null;
        }
        var rtspUrl = videoConfig.getRtspUrl();
        var result = videoAsyncTask.pickImage(channelGuid, rtspUrl);
        try {
            /*while(!result.isDone()){
                Thread.sleep(1000);
            }*/
            return result.get(30, TimeUnit.SECONDS); //等待3秒
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }
}
