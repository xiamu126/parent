package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampStrategyModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampStrategyMapper {
    int insert(LampStrategyModel model);
    int update(LampStrategyModel model);
    LampStrategyModel selectById(String id);
    List<LampStrategyModel> selectByOrganId(String organId);
    List<LampStrategyModel> selectByOrganIdStatus(@Param("organId") String organId, @Param("status") LampStrategyModel.Status status);
    List<LampStrategyModel> selectByCode(String code);
}
