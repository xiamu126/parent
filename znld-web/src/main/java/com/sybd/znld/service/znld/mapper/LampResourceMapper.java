package com.sybd.znld.service.znld.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.LampResourceModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface LampResourceMapper {
    int insert(LampResourceModel model);
    LampResourceModel selectByLampIdAndResourceId(String lampId, String resourceId);
    List<LampResourceModel> selectByLampId(String lampId);
}
