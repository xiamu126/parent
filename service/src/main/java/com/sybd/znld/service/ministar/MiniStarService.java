package com.sybd.znld.service.ministar;

import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.mapper.ministar.TwinkleBeautyGroupMapper;
import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;
import com.sybd.znld.znld.util.MyDateTime;
import com.sybd.znld.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MiniStarService implements IMiniStarService {
    private final TwinkleBeautyGroupMapper twinkleBeautyGroupMapper;
    private final RegionMapper regionMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MiniStarService(TwinkleBeautyGroupMapper twinkleBeautyGroupMapper, RegionMapper regionMapper) {
        this.twinkleBeautyGroupMapper = twinkleBeautyGroupMapper;
        this.regionMapper = regionMapper;
    }

    @Override
    public List<TwinkleBeautyGroupModel> getTwinkleBeautyGroupByRegionId(String regionId) {
        if(!MyString.isUuid(regionId)) return null;
        return this.twinkleBeautyGroupMapper.selectByRegionId(regionId);
    }

    @Override
    public TwinkleBeautyGroupModel addTwinkleBeautyGroup(TwinkleBeautyGroupModel model) {
        if(model == null) return null;
        if(!MyDateTime.isAllFutureAndStrictAsc(model.beginTime, model.endTime)){
            log.debug("错误的开始结束时间：["+model.beginTime+"],["+model.endTime+"]");
            return null;
        }
        if(!TwinkleBeautyGroupModel.Status.isValid(model.status)){
            log.debug("错误的状态");
            return null;
        }
        if(!MyString.isUuid(model.regionId)){
            log.debug("错误的区域id");
            return null;
        }
        if(this.regionMapper.selectById(model.regionId) == null){
            log.debug("指定的区域id不存在");
            return null;
        }
        // 如果指定的区域时间内已经存在了相关的非失效的节目组，新增失败
        var list = this.twinkleBeautyGroupMapper.selectActiveByRegionIdAndIntersectWith(model.regionId, model.beginTime, model.endTime);
        if(list != null && list.size() > 0){
            log.debug("指定的区域时间内已经存在有效的节目组，新增失败");
            return null;
        }
        if(this.twinkleBeautyGroupMapper.insert(model) > 0) return model;
        return null;
    }
}
