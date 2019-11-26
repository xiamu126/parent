package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampModule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampModuleMapper {
    int insert(LampModule module);
    int update(LampModule module);
    LampModule selectById(Integer id);
    LampModule selectByName(String name);
}
