package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.model.znld.RegionModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegionMapper {
    int insert(RegionModel model);
    RegionModel selectById(String id);
    RegionModel selectByName(String name);
    int updateById(RegionModel model);
}
