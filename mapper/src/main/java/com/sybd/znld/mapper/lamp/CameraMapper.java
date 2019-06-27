package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.CameraModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface CameraMapper {
    CameraModel selectById(String id);
    int updateById(CameraModel model);
    int insert(CameraModel model);
    CameraModel selectByRtspUrl(String rtspUrl);
    int deleteById(String id);
}
