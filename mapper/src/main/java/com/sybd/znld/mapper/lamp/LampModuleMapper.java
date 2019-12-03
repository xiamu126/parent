package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampModuleModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampModuleMapper {
    int insert(LampModuleModel module);
    int update(LampModuleModel module);
    LampModuleModel selectById(Integer id);
    LampModuleModel selectByName(String name);
    List<LampModuleModel> selectModulesByLampId(String id);
}
