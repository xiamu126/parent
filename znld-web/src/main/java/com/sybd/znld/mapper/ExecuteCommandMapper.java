package com.sybd.znld.mapper;

import com.sybd.znld.model.ExecuteCommandEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExecuteCommandMapper {
    ExecuteCommandEntity getByCommand(String value);
}
