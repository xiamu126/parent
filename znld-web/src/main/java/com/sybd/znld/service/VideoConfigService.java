package com.sybd.znld.service;

import com.sybd.znld.service.model.VideoConfigEntity;

public interface VideoConfigService extends BaseService {
    VideoConfigEntity getConfigByCameraId(String cameraId);
    int setConfigByCameraId(VideoConfigEntity videoConfigEntity);
}
