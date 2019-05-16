package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.AppModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface AppMapper {
    AppModel selectByName(String name);
}
