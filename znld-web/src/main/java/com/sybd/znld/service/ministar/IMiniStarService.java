package com.sybd.znld.service.ministar;

import com.sybd.ministar.model.TwinkleBeautyGroupModel;

import java.util.List;

public interface IMiniStarService {
    List<TwinkleBeautyGroupModel> getTwinkleBeautyGroupByRegionId(String regionId);
    TwinkleBeautyGroupModel addTwinkleBeautyGroup(TwinkleBeautyGroupModel model);
}
