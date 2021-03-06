package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampStrategyWaitingModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampStrategyWaitingMapper {
    int insert(LampStrategyWaitingModel model);
    int update(LampStrategyWaitingModel model);
    List<LampStrategyWaitingModel> selectByOrganIdStatus(@Param("organId") String organId,
                                                         @Param("status") LampStrategyWaitingModel.Status status);
    LampStrategyWaitingModel selectByLampIdStatus(@Param("lampId") String lampId,
                                                  @Param("status") LampStrategyWaitingModel.Status status);
    LampStrategyWaitingModel selectByLampId(String id);
    List<LampStrategyWaitingModel> selectByStatus(LampStrategyWaitingModel.Status status);
}
