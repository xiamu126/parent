package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampStrategyModel;
import com.sybd.znld.model.lamp.dto.LampStrategyBase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampStrategyMapper {
    int insert(LampStrategyModel model);
    int update(LampStrategyModel model);
    LampStrategyModel selectById(String id);
    List<LampStrategyModel> selectByName(String name);
    List<LampStrategyBase> selectOrganId(String id);
}
