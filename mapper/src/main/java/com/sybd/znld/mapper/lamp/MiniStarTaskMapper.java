package com.sybd.znld.mapper.lamp;

import com.sybd.znld.mapper.db.DbSource;
import com.sybd.znld.model.lamp.MiniStarTaskModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DbSource("znld")
public interface MiniStarTaskMapper {
    int insert(MiniStarTaskModel model);
    List<MiniStarTaskModel> selectByStatusWaitingAndOrganId(String organId);
    List<MiniStarTaskModel> selectByStatusWaiting();
    int updateStatus(MiniStarTaskModel model);
}
