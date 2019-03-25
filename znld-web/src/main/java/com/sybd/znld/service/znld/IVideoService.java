package com.sybd.znld.service.znld;

import com.sybd.znld.model.znld.VideoConfigModel;
import com.sybd.znld.service.IBaseService;
import com.sybd.znld.service.video.dto.VideoData;

import java.awt.image.BufferedImage;

public interface IVideoService extends IBaseService {
    VideoConfigModel getConfigByCameraId(String cameraId);
    VideoConfigModel setConfigByCameraId(VideoConfigModel model);
    boolean push(String channelGuid);
    void stop(String channelGuid);
    void verify();
    void stopAll();
    boolean isChannelInUsing(String channelGuid);
    BufferedImage pickImage(String channelGuid);
}
