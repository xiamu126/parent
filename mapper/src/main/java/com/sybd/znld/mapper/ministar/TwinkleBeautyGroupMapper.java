package com.sybd.znld.mapper.ministar;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;
import com.sybd.znld.model.ministar.dto.TwinkleHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    List<TwinkleHistory> selectMany(@Param("userId") String userId, @Param("count") Integer count);
    int updateStatusById(@Param("id")String id, @Param("status") Integer status);
}
