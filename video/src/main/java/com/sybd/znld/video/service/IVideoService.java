package com.sybd.znld.video.service;

import com.sybd.znld.model.lamp.CameraModel;
import com.sybd.znld.service.IBaseService;

import java.awt.image.BufferedImage;

public interface IVideoService extends IBaseService {
    CameraModel getCameraById(String cameraId);
    CameraModel setCameraById(CameraModel model);
    boolean push(String channelGuid);
    void stop(String channelGuid);
    void stopAll();
    boolean isChannelInUsing(String channelGuid);
    BufferedImage pickImage(String channelGuid);
    boolean heartbeat(String channelGuid);
}
