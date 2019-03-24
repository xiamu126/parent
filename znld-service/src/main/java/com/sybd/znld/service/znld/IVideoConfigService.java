package com.sybd.znld.service.znld;

import com.sybd.znld.model.znld.VideoConfigModel;
import com.sybd.znld.service.IBaseService;

public interface IVideoConfigService extends IBaseService {
    VideoConfigModel getConfigByCameraId(String cameraId);
    VideoConfigModel setConfigByCameraId(VideoConfigModel model);
}
