package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface ElectricityDispositionBoxMapper {
    int insert(ElectricityDispositionBoxModel model);
    int update(ElectricityDispositionBoxModel model);
    ElectricityDispositionBoxModel selectById(String id);
    ElectricityDispositionBoxModel selectByName(String name);
}
