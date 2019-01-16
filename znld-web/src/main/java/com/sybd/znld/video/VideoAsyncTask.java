package com.sybd.znld.video;

import com.sybd.znld.config.RedisKeyConfig;
import com.sybd.znld.service.RedisService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Slf4j
@Component
public class VideoAsyncTask {
    private final RedisService redisService;
    private static ConcurrentHashMap<String, Boolean> threadPools = new ConcurrentHashMap<>();

    @PreDestroy
    public void preDestroy(){
        log.debug("VideoAsyncTask关闭线程池");
        threadPools.clear();
    }

    @Autowired
    public VideoAsyncTask(RedisService redisService) {
        this.redisService = redisService;
    }

    public void stop(String channelGuid){
        if(threadPools.containsKey(channelGuid)){
            threadPools.put(channelGuid, true);
            threadPools.remove(channelGuid);
        }
        stopRedis(channelGuid);
        for (var entry : threadPools.entrySet()) {
            log.debug(entry.getKey() + " : " +  entry.getValue().toString());
        }
    }

    private boolean pushRedis(String channelGuid){
        var redisKey = RedisKeyConfig.CLIENT_CHANNEL_GUID_GLOBAL_PREFIX + channelGuid;
        if(redisService.exists(redisKey)){
            log.error("当前频道推流已经记录在全局缓存");
            return false;
        }
        redisService.set(redisKey, "");
        return true;
    }

    private void stopRedis(String channelGuid){
        var redisKey = RedisKeyConfig.CLIENT_CHANNEL_GUID_GLOBAL_PREFIX + channelGuid;
        redisService.delete(redisKey);
    }

    @Setter @Getter
    private static boolean terminateAll = false;

    void stopAll(){
        terminateAll = true;
        threadPools.clear();
    }

    @Async
    public Future<BufferedImage> pickImage(String channelGuid, String rtspPath){
        int width = 1920,height = 1080;
        FFmpegFrameGrabber grabber = null;
        try {
            if(threadPools.get(channelGuid) == null){
                threadPools.put(channelGuid, false);
                if(!pushRedis(channelGuid)){
                    log.debug("注册频道失败，"+channelGuid);
                }
                grabber = FFmpegFrameGrabber.createDefault(rtspPath);
                grabber.setOption("rtsp_transport", "tcp"); //使用tcp的方式，不然会丢包很严重
                grabber.setImageWidth(width);
                grabber.setImageHeight(height);
                grabber.setAudioStream(0);//海康威视的回放视频，回答带出一个stream 1的Audio输入，但为none
                grabber.start();
                var frame = grabber.grabImage();
                var converter = new Java2DFrameConverter();
                var bufferedImage = converter.convert(frame);
                return CompletableFuture.completedFuture(bufferedImage);
            }
        } catch (FrameGrabber.Exception e) {
            log.error(e.getMessage());
        } finally {
            stop(channelGuid);
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (FrameGrabber.Exception e) {
                log.error(e.getMessage());
            }
            log.debug("截取图片结束，结束视频推流");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public void push(String channelGuid, String rtspPath, String rtmpPath, int audioRecord) {
        int width = 1920,height = 1080;
        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;
        try {
            if(threadPools.get(channelGuid) != null){
                log.error("当前频道已经在推流");
                return;
            }
            threadPools.put(channelGuid, false);
            if(!pushRedis(channelGuid)){
                log.error("当前频道已经在全局缓存注册过，"+channelGuid);
                return;
            }
            grabber = FFmpegFrameGrabber.createDefault(rtspPath);
            grabber.setOption("rtsp_transport", "tcp"); //使用tcp的方式，不然会丢包很严重
            //一直报错的原因！！！就是因为是 2560 * 1440的太大了。。
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
            log.debug("grabber start");
            grabber.setAudioStream(0);//海康威视的回放视频，回答带出一个stream 1的Audio输入，但为none
            grabber.start();
            //流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
            recorder = new FFmpegFrameRecorder(rtmpPath, width, height, audioRecord);
            recorder.setInterleaved(true);
            recorder.setVideoOption("crf", "28");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); //28
            recorder.setFormat("flv"); //rtmp的类型
            recorder.setFrameRate(25);
            recorder.setPixelFormat(avutil.AV_PICTURE_TYPE_NONE);
            log.debug("recorder start");
            recorder.start();
            while(!terminateAll && threadPools.containsKey(channelGuid) && !threadPools.get(channelGuid)){
                Frame frame = grabber.grabImage();
                recorder.record(frame);
            }
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (FrameGrabber.Exception e) {
                log.error(e.getMessage());
            }

            try {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                }
            } catch (FrameRecorder.Exception e) {
                log.error(e.getMessage());
            }
            log.debug("exit1111");
        }
    }
}
