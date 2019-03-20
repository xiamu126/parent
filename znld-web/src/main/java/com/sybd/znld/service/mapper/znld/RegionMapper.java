package com.sybd.znld.service.mapper.znld;

import com.sybd.znld.service.model.znld.RegionModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegionMapper {
    int insert(RegionModel model);
    RegionModel selectById(String id);
    RegionModel selectByName(String name);
    int updateById(RegionModel model);
}
