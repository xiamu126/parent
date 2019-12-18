package com.sybd.znld.mapper.lamp;

import com.sybd.znld.model.StrategyFailedStatus;
import com.sybd.znld.model.lamp.StrategyFailedModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StrategyFailedMapper {
    int insert(StrategyFailedModel model);
    int update(StrategyFailedModel model);
    StrategyFailedModel selectById(String id);
    List<StrategyFailedModel> selectByStrategyId(String id);
    List<StrategyFailedModel> selectByStatus(StrategyFailedStatus status);
    List<StrategyFailedModel> selectByStrategyIdStatus(@Param("id") String id, @Param("status") StrategyFailedStatus status);
}
