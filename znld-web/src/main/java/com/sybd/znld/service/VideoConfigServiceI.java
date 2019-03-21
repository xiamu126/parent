package com.sybd.znld.service;

import com.sybd.znld.service.model.VideoConfigEntity;

public interface VideoConfigServiceI extends IBaseService {
    VideoConfigEntity getConfigByCameraId(String cameraId);
    int setConfigByCameraId(VideoConfigEntity videoConfigEntity);
}
