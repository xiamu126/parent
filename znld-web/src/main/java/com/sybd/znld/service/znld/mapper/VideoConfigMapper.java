package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.CameraModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface VideoConfigMapper {
    CameraModel getConfigByCameraId(String cameraId);
    int setConfigByCameraId(CameraModel model);
}
