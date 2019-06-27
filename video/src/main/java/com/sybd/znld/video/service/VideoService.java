package com.sybd.znld.video.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.mapper.ministar.TwinkleBeautyGroupMapper;
import com.sybd.znld.mapper.oauth.OAuthClientDetailsMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.mapper.lamp.CameraMapper;
import com.sybd.znld.model.lamp.CameraModel;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class VideoService extends BaseService implements IVideoService {
    private final RedissonClient redissonClient;
    private final CameraMapper cameraMapper;
    private final VideoAsyncTask videoAsyncTask;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public VideoService(CameraMapper cameraMapper,
                        CacheManager cacheManager,
                        TaskScheduler taskScheduler, ProjectConfig projectConfig,
                        RedissonClient redissonClient,
                        VideoAsyncTask videoAsyncTask, ObjectMapper objectMapper) {
        super(cacheManager, taskScheduler, projectConfig);
        this.cameraMapper = cameraMapper;
        this.redissonClient = redissonClient;
        this.videoAsyncTask = videoAsyncTask;
        this.objectMapper = objectMapper;
    }

    @PreDestroy
    public void preDestroy(){
        log.debug("VideoService关闭所有正在执行的任务");
        //this.stopAll();
    }

    @PostConstruct
    public void postConstruct(){
        log.debug("postConstruct");
    }

    @Override
    public CameraModel getCameraById(String cameraId) {
        if(!MyString.isUuid(cameraId)) return null;
        return this.cameraMapper.selectById(cameraId);
    }

    @Override
    public CameraModel setCameraById(CameraModel model) {
        if(model == null || !MyString.isUuid(model.id)) return null;
        if(cameraMapper.updateById(model) > 0) return model;
        return null;
    }

    @Override
    public boolean push(String channelGuid) {
        if(!MyString.isUuid(channelGuid)) {
            log.error("channelGuid错误，"+channelGuid);
            return false;
        }
        try {
            var camera = cameraMapper.selectById(channelGuid);
            if(camera == null){
                log.error("获取摄像头配置为空，"+channelGuid);
                return false;
            }
            var rtspUrl = camera.rtspUrl;
            var rtmp = objectMapper.readValue(camera.rtmp, CameraModel.Rtmp.class);
            if(rtmp == null) return false;
            var recordAudio = camera.recordAudio;
            this.videoAsyncTask.push(channelGuid, rtspUrl, rtmp.liveUrl, recordAudio ? 1 : 0);
            try{
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
            }catch (InterruptedException ex) {
                log.error(ex.getMessage()); // 发生超时异常，意味着推流成功，进入了循环状态
            }
            var tmp = this.videoAsyncTask.isChannelInUsing(channelGuid);
            if(!tmp){
                log.error("已经推送，但isChannelInUsing返回false");
            }
            return tmp;
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
       return false;
    }

    @Override
    public void stop(String channelGuid){
        this.videoAsyncTask.stop(channelGuid);
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
        return videoAsyncTask.pickImage(channelGuid);
    }

    @Override
    public boolean heartbeat(String channelGuid) {
        return this.videoAsyncTask.heartbeat(channelGuid);
    }
}
