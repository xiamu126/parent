package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampCameraModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@DbSource("znld")
public interface LampCameraMapper {
    int insert(LampCameraModel model);
    LampCameraModel selectById(String id);
    LampCameraModel selectByCameraId(String cameraId);
    int deleteById(String id);
    int deleteByLampIdAndCameraId(@Param("lampId") String lampId, @Param("cameraId") String cameraId);
}
