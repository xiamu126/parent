package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampStrategyTargetModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampStrategyTargetMapper {
    int insert(LampStrategyTargetModel model);
    int update(LampStrategyTargetModel model);
    LampStrategyTargetModel selectById(String id);
    List<LampStrategyTargetModel> selectByStrategyId(String id);
}
