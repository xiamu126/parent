package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.StrategyPointModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface StrategyPointMapper {
    int insert(StrategyPointModel model);
    int update(StrategyPointModel model);
    StrategyPointModel selectById(String id);
    List<StrategyPointModel> selectByStrategyId(String id);
}
