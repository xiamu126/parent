package com.sybd.znld.video;

import com.sybd.znld.video.dto.VideoData;

import java.awt.image.BufferedImage;

public interface IVideoRepository {
    void stop(String channelGuid);
    void push(VideoData videoData);
    void stopAll();
    BufferedImage pickImage(String channelGuid);
}
