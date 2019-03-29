package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.VideoConfigModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface VideoConfigMapper {
    VideoConfigModel getConfigByCameraId(String cameraId);
    int setConfigByCameraId(VideoConfigModel model);
}
