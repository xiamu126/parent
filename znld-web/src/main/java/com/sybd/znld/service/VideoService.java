package com.sybd.znld.service;

import com.sybd.znld.video.dto.VideoData;

import java.awt.image.BufferedImage;

public interface VideoService {
    boolean push(String key, VideoData input);
    boolean stop(String key, VideoData input);
    void verify();
    void stopAll();
    boolean isChannelInUsing(String channelGuid);
    BufferedImage pickImage(String channelGuid);
}
