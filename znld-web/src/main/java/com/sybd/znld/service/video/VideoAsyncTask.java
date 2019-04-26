package com.sybd.znld.service.video;

import com.sybd.znld.config.RedisKeyConfig;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
@Component
public class VideoAsyncTask {
    private final RedissonClient redissonClient;
    private final RLock locker;
    private static final int RECURSIVE_DEPTH = 5;

    @PreDestroy
    public void preDestroy(){
        log.debug("VideoAsyncTask关闭线程池");
    }

    @Autowired
    public VideoAsyncTask(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.locker = this.redissonClient.getLock(RedisKeyConfig.RLOCK_CHANNEL_GLOBAL_REGISTER);
    }

    public void  stop(String channelGuid){
        globalUnregister(channelGuid);
    }

    private String getGlobalKey(String channelGuid){
        return RedisKeyConfig.CLIENT_CHANNEL_GUID_GLOBAL_PREFIX + channelGuid;
    }

    private String globalRegister(String channelGuid){
        var globalKey = getGlobalKey(channelGuid);
        if(redissonClient.getKeys().countExists(globalKey) > 0){ // 存在，不能再次注册
            return null;
        }
        redissonClient.getBucket(globalKey).set("init", 59, TimeUnit.SECONDS); //持续59秒，若无心跳则自动结束
        return globalKey;
    }

    private void globalUnregister(String channelGuid){
        var globalKey = getGlobalKey(channelGuid);
        redissonClient.getKeys().delete(globalKey);
    }

    public void stopAll(){
        redissonClient.getKeys().deleteByPattern(RedisKeyConfig.CLIENT_CHANNEL_GUID_GLOBAL_PREFIX_MATCH);
    }

    public boolean isChannelInUsing(String channelGuid){
        var globalKey = getGlobalKey(channelGuid);
        return redissonClient.getKeys().countExists(globalKey) > 0;
    }

    public boolean heartbeat(String channelGuid){
        var globalKey = getGlobalKey(channelGuid);
        if(redissonClient.getKeys().countExists(globalKey) > 0){
            log.debug("当前频道不存在，" + channelGuid);
            return false;
        }
        redissonClient.getBucket(globalKey).set("heartbeat", 59, TimeUnit.SECONDS); //持续59秒，若无心跳则自动结束
        return true;
    }

    @Async
    public Future<BufferedImage> pickImage(String channelGuid, String rtspPath){
        int width = 1920,height = 1080;
        var globalKey = globalRegister(channelGuid);
        if(globalKey != null){// 如果已经有在看视频，那么截图就不能用了
            FFmpegFrameGrabber grabber = null;
            try {
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
            } catch (FrameGrabber.Exception ex) {
                log.error(ex.getMessage());
            } finally {
                try {
                    if (grabber != null) {
                        grabber.stop();
                        grabber.release();
                    }
                } catch (FrameGrabber.Exception ex) {
                    log.error(ex.getMessage());
                }
                log.debug("截取图片结束，结束视频推流");
                globalUnregister(channelGuid);
            }
        }
        // 其它情况意味着错误
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public void push(String channelGuid, String rtspPath, String rtmpPath, int audioRecord) {
        int width = 1920,height = 1080;
        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;
        try {
            /*
            考虑以下情况，如果有连个Web实例（A和B）同时推送一个频道，它们共用一个全局标记，但真正在推流的是A，B是共用了对应的视频频道
            如果A收到了停止的指令，它能不能停止推流呢？显然是不能的，因为B并没有真正推流，如果A关闭了推流，B那一头的视频也就结束了
            也就是说，如果对A来说，当它收到停止指令的时候，如果还有其他人共享这个频道时，是不能真正结束的；
            也就是全局标记是决定推流与否的关键，而本地维护一份频道记录就没有必要了
            * */
            //推流继续与否主要看这个全局标记，本地的频道记录
            var globalKey = globalRegister(channelGuid);
            if(globalKey == null){
                log.debug("获取globalKey失败，当前频道已经在全局缓存注册过，" + channelGuid);
                return;
            }
            grabber = FFmpegFrameGrabber.createDefault(rtspPath);
            grabber.setOption("rtsp_transport", "tcp"); //使用tcp的方式，不然会丢包很严重
            //一直报错的原因！！！就是因为是 2560 * 1440的太大了。。
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
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
            recorder.start();
            var keys = redissonClient.getKeys();
            while (keys.countExists(globalKey) > 0) {
                //每个运行中的线程必然关联一个全局标记，因此若全局标记失效则立即结束线程
                var frame = grabber.grabImage();
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
            log.debug("视频推流结束");
            globalUnregister(channelGuid);
        }
    }
}
