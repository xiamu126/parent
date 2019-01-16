package com.sybd.znld.mapper;

import com.sybd.znld.model.VideoConfigEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoConfigMapper {
    VideoConfigEntity getConfigByCameraId(String cameraId);
    int setConfigByCameraId(VideoConfigEntity videoConfigEntity);
}
