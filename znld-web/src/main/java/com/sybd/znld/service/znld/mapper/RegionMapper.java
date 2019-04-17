package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.RegionModel;
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
