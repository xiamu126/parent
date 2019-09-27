package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.MiniStarEffectModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DbSource("znld")
public interface MiniStarEffectMapper {
    int insert(MiniStarEffectModel model);
    int update(MiniStarEffectModel model);
    List<MiniStarEffectModel> selectByOrganId(String organId);
    MiniStarEffectModel selectByName(String name);
    int deleteByIds(@Param("organId") String organId, @Param("ids") List<Integer> ids);
    int deleteById(@Param("organId") String organId, @Param("id") Integer id);
}
