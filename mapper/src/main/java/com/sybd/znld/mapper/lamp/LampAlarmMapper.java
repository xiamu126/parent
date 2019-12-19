package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampAlarmModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampAlarmMapper {
    int insert(LampAlarmModel model);
    int update(LampAlarmModel model);
    LampAlarmModel selectById(String id);
    List<LampAlarmModel> selectByOrganId(String id);
}
