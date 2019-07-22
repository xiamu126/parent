package com.sybd.znld.mapper.ministar;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.ministar.TwinkleBeautyModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("ministar")
public interface TwinkleBeautyMapper {
    int insert(TwinkleBeautyModel model);
    TwinkleBeautyModel selectById(String id);
    List<TwinkleBeautyModel> selectByGroupId(String id);
}
