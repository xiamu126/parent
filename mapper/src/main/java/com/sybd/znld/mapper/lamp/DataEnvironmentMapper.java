package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.onenet.DataPushModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface DataEnvironmentMapper {
    int insert(DataPushModel model);
}
