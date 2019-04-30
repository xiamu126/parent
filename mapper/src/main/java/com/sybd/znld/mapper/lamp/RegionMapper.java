package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.RegionModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface RegionMapper {
    int insert(RegionModel model);
    RegionModel selectById(String id);
    RegionModel selectByName(String name);
    List<RegionModel> selectAll();
    RegionModel selectOne();
    List<RegionModel> select(int count);
    int updateById(RegionModel model);
    List<RegionModel> selectAllRegionWithValidLamp(String organId);
}
