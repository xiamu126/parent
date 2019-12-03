package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxModel;
import com.sybd.znld.model.lamp.LampModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface ElectricityDispositionBoxMapper {
    int insert(ElectricityDispositionBoxModel model);
    int update(ElectricityDispositionBoxModel model);
    ElectricityDispositionBoxModel selectById(String id);
    ElectricityDispositionBoxModel selectByName(String name);
    // 根据配电箱下面的所有路灯
    List<LampModel> selectLampsByBoxId(String id);
}
