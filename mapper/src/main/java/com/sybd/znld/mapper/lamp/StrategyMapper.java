package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.Strategy;
import com.sybd.znld.model.lamp.StrategyModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("znld")
public interface StrategyMapper {
    int insert(StrategyModel model);
    int update(StrategyModel model);
    StrategyModel selectById(String id);
    List<StrategyModel> selectByName(String name);
    List<StrategyModel> selectByOrganId(String id);
    List<StrategyModel> selectByOrganIdType(@Param("id") String id, @Param("type") Strategy type);
}