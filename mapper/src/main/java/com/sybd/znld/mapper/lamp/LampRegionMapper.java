package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampRegionModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface LampRegionMapper {
    int insert(LampRegionModel model);
    LampRegionModel selectById(String id);
}
