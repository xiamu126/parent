package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.ExecuteCommandModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DbSource("znld")
public interface ExecuteCommandMapper {
    ExecuteCommandModel selectById(Integer id);
    ExecuteCommandModel selectByValue(String value);
}
