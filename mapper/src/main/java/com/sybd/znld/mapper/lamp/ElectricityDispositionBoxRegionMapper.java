package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxRegionModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface ElectricityDispositionBoxRegionMapper {
    int insert(ElectricityDispositionBoxRegionModel model);
    int update(ElectricityDispositionBoxRegionModel model);
    ElectricityDispositionBoxRegionModel selectById(String id);
    List<ElectricityDispositionBoxRegionModel> selectByRegionId(String id);
    ElectricityDispositionBoxRegionModel selectByBoxId(String id);
}
