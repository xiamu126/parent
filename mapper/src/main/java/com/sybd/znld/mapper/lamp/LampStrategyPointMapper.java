package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampStrategyPointModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampStrategyPointMapper {
    int insert(LampStrategyPointModel model);
    int update(LampStrategyPointModel model);
    LampStrategyPointModel selectById(String id);
    List<LampStrategyPointModel> selectByStrategyId(String id);
}
