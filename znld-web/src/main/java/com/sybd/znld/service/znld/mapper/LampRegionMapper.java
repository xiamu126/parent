package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.LampRegionModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface LampRegionMapper {
    int insert(LampRegionModel model);
    LampRegionModel selectById(String id);
}
