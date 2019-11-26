package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.ElectricityDispositionBoxLampModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface ElectricityDispositionBoxLampMapper {
    int insert(ElectricityDispositionBoxLampModel model);
    int update(ElectricityDispositionBoxLampModel model);
    ElectricityDispositionBoxLampModel selectById(String id);
    List<ElectricityDispositionBoxLampModel> selectByBoxId(String id);
}
