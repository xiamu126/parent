package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.MiniStarEffectModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface MiniStarEffectMapper {
    int insert(MiniStarEffectModel model);
    List<MiniStarEffectModel> selectByOrganId(String organId);
    MiniStarEffectModel selectByName(String name);
}
