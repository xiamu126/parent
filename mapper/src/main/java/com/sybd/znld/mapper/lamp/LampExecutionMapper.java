package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampExecutionModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampExecutionMapper {
    int insert(LampExecutionModel model);
    int update(LampExecutionModel model);
    LampExecutionModel selectById(String id);
    LampExecutionModel selectByLampId(String id);
    List<LampExecutionModel> selectByStatus(LampExecutionModel.Status status);
    List<LampExecutionModel> selectByOrganId(String id);
}
