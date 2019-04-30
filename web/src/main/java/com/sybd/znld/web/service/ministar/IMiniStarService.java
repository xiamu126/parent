package com.sybd.znld.web.service.ministar;

import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;

import java.util.List;

public interface IMiniStarService {
    List<TwinkleBeautyGroupModel> getTwinkleBeautyGroupByRegionId(String regionId);
    TwinkleBeautyGroupModel addTwinkleBeautyGroup(TwinkleBeautyGroupModel model);
}
