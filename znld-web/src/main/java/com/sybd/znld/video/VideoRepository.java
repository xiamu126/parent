package com.sybd.znld.video;

import com.sybd.znld.video.dto.VideoData;
import com.sybd.znld.service.VideoConfigService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class VideoRepository implements IVideoRepository {

    private final VideoAsyncTask videoAsyncTask;
    private final VideoConfigService videoConfigService;

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
        var channelGuid = videoData.getChannelGuid();
        var tmp = videoConfigService.getConfigByCameraId(videoData.getChannelGuid());
        if(tmp == null){
            log.error("获取摄像头配置为空，"+channelGuid);
            return;
        }
        var rtspUrl = tmp.getRtspUrl();
        var rtmpUrl = tmp.getRtmpUrl();
        var recordAudio = tmp.getRecordAudio();
        videoAsyncTask.push(channelGuid, rtspUrl, rtmpUrl, recordAudio ? 1 : 0);
    }

    @Override
    public void stopAll() {
        videoAsyncTask.stopAll();
    }

    @Override
    public BufferedImage pickImage(String channelGuid) {
        var tmp = videoConfigService.getConfigByCameraId(channelGuid);
        if(tmp == null){
            log.error("获取摄像头配置为空，"+channelGuid);
            return null;
        }
        var rtspUrl = tmp.getRtspUrl();
        var result = videoAsyncTask.pickImage(channelGuid, rtspUrl);
        try {
            while(!result.isDone()){
                Thread.sleep(1000);
            }
            return result.get(1, TimeUnit.SECONDS); //等待3秒
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}

