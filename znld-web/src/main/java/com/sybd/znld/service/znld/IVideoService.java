package com.sybd.znld.service.znld;

import com.sybd.znld.model.znld.CameraModel;
import com.sybd.znld.service.IBaseService;

import java.awt.image.BufferedImage;

public interface IVideoService extends IBaseService {
    CameraModel getConfigByCameraId(String cameraId);
    CameraModel setConfigByCameraId(CameraModel model);
    boolean push(String channelGuid);
    void stop(String channelGuid);
    void verify();
    void stopAll();
    boolean isChannelInUsing(String channelGuid);
    BufferedImage pickImage(String channelGuid);
}
