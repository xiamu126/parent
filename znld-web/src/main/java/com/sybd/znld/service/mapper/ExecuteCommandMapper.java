package com.sybd.znld.service.mapper;

import com.sybd.znld.service.model.ExecuteCommandEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExecuteCommandMapper {
    ExecuteCommandEntity getByCommand(String value);
}
