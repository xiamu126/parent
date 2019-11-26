package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampLampModuleModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampLampModuleMapper {
    int insert(LampLampModuleModel model);
    int update(LampLampModuleModel model);
    int selectById(String id);
    List<LampLampModuleModel> selectByLampId(String id);
}
