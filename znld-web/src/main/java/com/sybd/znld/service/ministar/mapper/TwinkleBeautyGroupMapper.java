package com.sybd.znld.service.ministar.mapper;

import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TwinkleBeautyGroupMapper {
    int insert(TwinkleBeautyGroupModel model);
    TwinkleBeautyGroupModel selectById(String id);
    List<TwinkleBeautyGroupModel> selectByRegionId(String regionId);
    List<TwinkleBeautyGroupModel> selectActiveByRegionId(String regionId);
}
