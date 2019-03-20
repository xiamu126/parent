package com.sybd.znld.video;

import com.sybd.znld.service.model.VideoConfigEntity;
import com.sybd.znld.video.dto.VideoData;
import com.sybd.znld.service.VideoConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class VideoRepository implements IVideoRepository {

    private final VideoAsyncTask videoAsyncTask;
    private final VideoConfigService videoConfigService;
    private final Logger log = LoggerFactory.getLogger(VideoRepository.class);

    @PreDestroy
    public void preDestroy(){
        log.debug("VideoRepository关闭所有正在执行的任务");
        this.stopAll();
    }

    @Autowired
    public VideoRepository(VideoAsyncTask videoAsyncTask, VideoConfigService videoConfigService) {
        this.videoAsyncTask = videoAsyncTask;
        this.videoConfigService = videoConfigService;
    }

    @Override
    public void stop(String channelGuid) {
        videoAsyncTask.stop(channelGuid);
    }

    @Override
    public void push(VideoData videoData) {
        String channelGuid = videoData.getChannelGuid();
        VideoConfigEntity tmp = videoConfigService.getConfigByCameraId(videoData.getChannelGuid());
        if(tmp == null){
            log.error("获取摄像头配置为空，"+channelGuid);
            return;
        }
        String rtspUrl = tmp.getRtspUrl();
        String rtmpUrl = tmp.getRtmpUrl();
        Boolean recordAudio = tmp.getRecordAudio();
        videoAsyncTask.push(channelGuid, rtspUrl, rtmpUrl, recordAudio ? 1 : 0);
    }

    @Override
    public void stopAll() {
        videoAsyncTask.stopAll();
    }

    @Override
    public boolean isChannelInUsing(String channelGuid) {
        return this.videoAsyncTask.isChannelInUsing(channelGuid);
    }

    @Override
    public BufferedImage pickImage(String channelGuid) {
        VideoConfigEntity tmp = videoConfigService.getConfigByCameraId(channelGuid);
        if(tmp == null){
            log.error("获取摄像头配置为空，"+channelGuid);
            return null;
        }
        String rtspUrl = tmp.getRtspUrl();
        Future<BufferedImage> result = videoAsyncTask.pickImage(channelGuid, rtspUrl);
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

