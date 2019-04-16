package com.sybd.znld.service.ministar.mapper;

import com.sybd.znld.db.DbSource;
import com.sybd.ministar.model.TwinkleBeautyGroupModel;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@DbSource("ministar")
public interface TwinkleBeautyGroupMapper {
    int insert(TwinkleBeautyGroupModel model);
    TwinkleBeautyGroupModel selectById(String id);
    List<TwinkleBeautyGroupModel> selectByRegionId(String regionId);
    List<TwinkleBeautyGroupModel> selectActiveByRegionId(String regionId);
    List<TwinkleBeautyGroupModel> selectActiveByRegionIdAndIntersectWith(String regionId, LocalDateTime beginTime, LocalDateTime endTime);
}
