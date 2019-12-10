package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampStatisticsModel;
import com.sybd.znld.model.lamp.dto.Statistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@DbSource("znld")
public interface LampStatisticsMapper {
    int insert(LampStatisticsModel model);
    //int update(LampStatisticsModel model);
    //LampStatisticsModel selectById(String id);
    //List<LampStatisticsModel> selectByLampId(String id);
    //List<LampStatisticsModel> selectByLampIdBetween(@Param("id") String id, @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
