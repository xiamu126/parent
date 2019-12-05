package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.Status;
import com.sybd.znld.model.StrategyStatus;
import com.sybd.znld.model.lamp.Strategy;
import com.sybd.znld.model.lamp.StrategyModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
@DbSource("znld")
public interface StrategyMapper {
    int insert(StrategyModel model);
    int update(StrategyModel model);
    StrategyModel selectById(String id);
    List<StrategyModel> selectByName(String name);
    List<StrategyModel> selectByOrganIdTypeStatus(@Param("id") String id,
                                                  @Param("type") Strategy type,
                                                  @Param("status") StrategyStatus status);
    List<StrategyModel> selectByOrganIdTypeStatusBetween(@Param("id") String id,
                                                         @Param("type") Strategy type,
                                                         @Param("status") StrategyStatus status,
                                                         @Param("begin") LocalDate begin,
                                                         @Param("end") LocalDate end);
    List<StrategyModel> selectByOrganId(String id);
    List<StrategyModel> selectByOrganIdType(@Param("id") String id, @Param("type") Strategy type);
    List<StrategyModel> selectByOrganIdTypeNotStatus(@Param("id") String id, @Param("type") Strategy type, @Param("status") StrategyStatus status);
}