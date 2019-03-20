package com.sybd.znld.service.mapper;

import com.sybd.znld.service.model.VideoConfigEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoConfigMapper {
    VideoConfigEntity getConfigByCameraId(String cameraId);
    int setConfigByCameraId(VideoConfigEntity videoConfigEntity);
}
