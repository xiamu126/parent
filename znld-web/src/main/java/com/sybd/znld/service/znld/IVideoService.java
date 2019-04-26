package com.sybd.znld.service.znld;

import com.sybd.znld.model.CameraModel;
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
