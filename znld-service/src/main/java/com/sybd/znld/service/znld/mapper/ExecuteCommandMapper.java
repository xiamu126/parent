package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.model.znld.ExecuteCommandModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExecuteCommandMapper {
    ExecuteCommandModel selectById(Integer id);
    ExecuteCommandModel selectByValue(String value);
}
