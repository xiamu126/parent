package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.StrategyTargetModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface StrategyTargetMapper {
    int insert(StrategyTargetModel model);
    int update(StrategyTargetModel model);
    StrategyTargetModel selectById(String id);
    List<StrategyTargetModel> selectByStrategyId(String id);
}
