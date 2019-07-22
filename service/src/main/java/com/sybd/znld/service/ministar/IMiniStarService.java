package com.sybd.znld.service.ministar;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;
import com.sybd.znld.model.onenet.dto.OneNetExecuteArgs;

import java.util.List;
import java.util.Map;

public interface IMiniStarService {
    List<TwinkleBeautyGroupModel> getTwinkleBeautyGroupByRegionId(String regionId);
    TwinkleBeautyGroupModel addTwinkleBeautyGroup(TwinkleBeautyGroupModel model);
    Map<Integer, BaseApiResult> newMiniStar(List<OneNetExecuteArgs> data);
}
