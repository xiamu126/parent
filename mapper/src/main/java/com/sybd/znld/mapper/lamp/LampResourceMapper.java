package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.LampResourceModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampResourceMapper {
    int insert(LampResourceModel model);
    LampResourceModel selectByLampIdAndResourceId(@Param("lampId") String lampId, @Param("resourceId") String resourceId);
    List<LampResourceModel> selectByLampId(String lampId);
}
