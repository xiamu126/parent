package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.model.znld.VideoConfigModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoConfigMapper {
    VideoConfigModel getConfigByCameraId(String cameraId);
    int setConfigByCameraId(VideoConfigModel model);
}
