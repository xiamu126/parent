package com.sybd.znld.video.service;

import com.sybd.znld.video.config.RedisKeyConfig;
import com.sybd.znld.znld.util.MyNumber;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VideoAsyncTask {
    private final RedissonClient redissonClient;
    private final RLock locker;
    private static final int RECURSIVE_DEPTH = 50;
    private static ConcurrentHashMap<String ,BufferedImage> images = new ConcurrentHashMap<>(1024);

    @PreDestroy
    public void preDestroy(){
        log.debug("VideoAsyncTask关闭线程池");
    }

    @Autowired
    public VideoAsyncTask(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.locker = this.redissonClient.getLock(RedisKeyConfig.RLOCK_CHANNEL_GLOBAL_REGISTER);
    }

    public void stop(String channelGuid){
        globalUnregister(channelGuid);
    }

    private String getGlobalKey(String channelGuid){
        return RedisKeyConfig.CLIENT_CHANNEL_GUID_GLOBAL_PREFIX + channelGuid;
    }

    private String globalRegister(String channelGuid){
        if(isChannelInUsing(channelGuid)){ // 存在，不能再次注册
            return null;
        }
        var globalKey = getGlobalKey(channelGuid);
        var map = redissonClient.getMapCache(globalKey);
        var count = MyNumber.getLong(map.get("init"));
        map.put("init", count == null ? 0 : ++count);
        map.put("heartbeat", 0, 59, TimeUnit.SECONDS);
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
        if(redissonClient.getKeys().countExists(globalKey) <= 0){
            return false;
        }
        var map = redissonClient.getMapCache(globalKey);
        if(map == null) return isChannelInUsing(channelGuid, 0);
        var tmp = map.get("heartbeat");
        return tmp != null;
    }
    public boolean isChannelInUsing(String channelGuid, int depth){
        var globalKey = getGlobalKey(channelGuid);
        if(redissonClient.getKeys().countExists(globalKey) <= 0){
            return false;
        }
        var map = redissonClient.getMapCache(globalKey);
        if(map == null) {
            if(depth < RECURSIVE_DEPTH) {
                return isChannelInUsing(channelGuid, ++depth);
            }
            throw new RuntimeException("获取redis map失败");
        }else {
            var tmp = map.get("heartbeat");
            return tmp != null;
        }
    }

    public boolean heartbeat(String channelGuid){
        var globalKey = getGlobalKey(channelGuid);
        if(redissonClient.getKeys().countExists(globalKey) <= 0){
            return false;
        }
        var map = redissonClient.getMapCache(globalKey);
        if(map == null) return false;
        var count = MyNumber.getLong(map.get("heartbeat"));
        map.put("heartbeat", count == null ? 0 : ++count, 59, TimeUnit.SECONDS); //持续59秒，若无心跳则自动结束
        //redissonClient.getBucket(globalKey).set("heartbeat", 59, TimeUnit.SECONDS); //持续59秒，若无心跳则自动结束
        count = MyNumber.getLong(map.get("heartbeat"));
        log.debug(count == null ? "null" : count.toString());
        return true;
    }

    public void saveImage(String channelGuid, BufferedImage image){
        images.put(channelGuid, image);
    }

    public BufferedImage pickImage(String channelGuid){
        return images.get(channelGuid);
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
                log.debug("获取globalKey失败，当前频道已经在推流中，" + channelGuid);
                return;
            }
            grabber = FFmpegFrameGrabber.createDefault(rtspPath);
            grabber.setOption("rtsp_transport", "tcp"); //使用tcp的方式，不然会丢包很严重
            //一直报错的原因！！！就是因为是 2560 * 1440的太大了。。
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
            grabber.setAudioStream(0);//海康威视的回放视频，回答带出一个stream 1的Audio输入，但为none
            log.debug("grabber start");
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
            var converter = new Java2DFrameConverter();
            while (isChannelInUsing(channelGuid)) {
                //每个运行中的线程必然关联一个全局标记，因此若全局标记失效则立即结束线程
                var frame = grabber.grabImage();
                var bufferedImage = converter.convert(frame); // 保存一份图片
                saveImage(channelGuid, bufferedImage);
                recorder.record(frame);
            }
            log.debug("视频推流结束");
        } catch (FrameGrabber.Exception | FrameRecorder.Exception ex) {
            log.error(ex.getMessage());
            Arrays.stream(ex.getStackTrace()).forEach(t -> log.error(t.toString()));
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
        }
    }
}
