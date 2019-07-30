package com.sybd.znld.service.ministar;

import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.mapper.ministar.TwinkleBeautyGroupMapper;
import com.sybd.znld.mapper.ministar.TwinkleBeautyMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.RegionModel;
import com.sybd.znld.model.ministar.TwinkleBeautyGroupModel;
import com.sybd.znld.model.ministar.TwinkleBeautyModel;
import com.sybd.znld.model.ministar.dto.Subtitle;
import com.sybd.znld.model.ministar.dto.TwinkleHistory;
import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.model.onenet.dto.OneNetExecuteArgs;
import com.sybd.znld.model.rbac.UserModel;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MiniStarService implements IMiniStarService {
    private final TwinkleBeautyGroupMapper twinkleBeautyGroupMapper;
    private final TwinkleBeautyMapper twinkleBeautyMapper;
    private final RegionMapper regionMapper;
    private final UserMapper userMapper;
    private final LampMapper lampMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final IOneNetService oneNetService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MiniStarService(TwinkleBeautyGroupMapper twinkleBeautyGroupMapper,
                           TwinkleBeautyMapper twinkleBeautyMapper,
                           RegionMapper regionMapper,
                           UserMapper userMapper,
                           LampMapper lampMapper,
                           OneNetResourceMapper oneNetResourceMapper,
                           IOneNetService oneNetService) {
        this.twinkleBeautyGroupMapper = twinkleBeautyGroupMapper;
        this.twinkleBeautyMapper = twinkleBeautyMapper;
        this.regionMapper = regionMapper;
        this.userMapper = userMapper;
        this.lampMapper = lampMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.oneNetService = oneNetService;
    }

    @Override
    public List<TwinkleBeautyGroupModel> getTwinkleBeautyGroupByRegionId(String regionId) {
        if(!MyString.isUuid(regionId)) return null;
        return this.twinkleBeautyGroupMapper.selectByRegionId(regionId);
    }

    @Override
    public TwinkleBeautyGroupModel addTwinkleBeautyGroup(TwinkleBeautyGroupModel model) {
        if(model == null) return null;
        if(!MyDateTime.isAllFutureAndStrict(model.beginTime, model.endTime)){
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

    @Override
    @Transactional(rollbackFor = { Exception.class }, transactionManager = "ministarTransactionManager")
    public Map<Integer, BaseApiResult> miniStar(List<OneNetExecuteArgs> data) {
        var map = new HashMap<Integer, BaseApiResult>();
        if(data == null || data.isEmpty()) {
            return null;
        }
        for(var arg : data){
            if(!arg.isValid()) {
                return null;
            }
            var subtitle = arg.getSubtitle();
            var user = this.userMapper.selectById(subtitle.userId);
            if(user == null) {
                return null;
            }
            String regionId = null;
            if(MyString.isUuid(subtitle.region)){
                regionId = subtitle.region;
            }else{ // 可能为区域名称
                var region = this.regionMapper.selectByName(subtitle.region);
                if(region != null) regionId = region.id;
            }
            if(regionId == null) {
                return null;
            }
            var resource = this.oneNetResourceMapper.selectByCommandValue(Command.ZNLD_DD_EXECUTE);
            if(resource == null) {
                return null;
            }
            var lamps = this.lampMapper.selectByRegionId(regionId);
            if(lamps == null || lamps.isEmpty()) {
                return null;
            }
            for (var lamp : lamps) {
                var params = new CommandParams();
                params.deviceId = lamp.deviceId;
                params.imei = lamp.imei;
                params.oneNetKey = resource.toOneNetKey();
                params.timeout = resource.timeout;
                params.command = arg.getPackedCmd();
                var ret = oneNetService.execute(params);
                map.put(lamp.deviceId, new BaseApiResult(ret.errno, ret.error));
            }

            // 如果新建灯带效果，则需要添加历史记录
            if(arg.isNewMiniStar()){
                var tmp = new TwinkleBeautyGroupModel();
                tmp.beginTime = MyDateTime.toLocalDateTime(subtitle.beginTimestamp);
                tmp.endTime = MyDateTime.toLocalDateTime(subtitle.endTimestamp);
                tmp.regionId = regionId;
                tmp.status = TwinkleBeautyGroupModel.Status.RUNNING;
                this.twinkleBeautyGroupMapper.insert(tmp);

                var tmp2 = new TwinkleBeautyModel();
                tmp2.title = subtitle.title;
                tmp2.userId = subtitle.userId;
                tmp2.color = subtitle.effect.getColor();
                tmp2.rate = subtitle.speed;
                tmp2.type = subtitle.effect.type;
                tmp2.twinkleBeautyGroupId = tmp.id;
                this.twinkleBeautyMapper.insert(tmp2);
            }
            if(arg.isStopMiniStar()){ //如果是关闭则需要设置相关的状态按钮
                this.twinkleBeautyGroupMapper.updateStatusById(subtitle.historyId, (int)TwinkleBeautyGroupModel.Status.STOPPED);
            }
        }
        return map;
    }

    @Override
    public List<TwinkleHistory> history(String userId, int count) {
        var ret = this.twinkleBeautyGroupMapper.selectMany(userId, count);
        Map<String, RegionModel> regions = new HashMap<>();
        Map<String, UserModel> users = new HashMap<>();
        if(ret != null){
            for(var t : ret){
                if(MyString.isUuid(t.regionId)){
                    var region = regions.get(t.regionId);
                    if(region == null){
                        var tmp = this.regionMapper.selectById(t.regionId);
                        regions.put(t.regionId, tmp);
                        t.regionName = tmp.name;
                    }else{
                        t.regionName = region.name;
                    }
                }
                if(MyString.isUuid(t.userId)){
                    var user = users.get(t.userId);
                    if(user == null){
                        var tmp = this.userMapper.selectById(t.userId);
                        users.put(t.userId, tmp);
                        t.userName = tmp.name;
                    }else {
                        t.userName = user.name;
                    }
                }
                if(MyDateTime.isPast(t.endTime)){
                    if(!TwinkleBeautyGroupModel.Status.isStopped(t.status)){
                        this.twinkleBeautyGroupMapper.updateStatusById(t.id, (int) TwinkleBeautyGroupModel.Status.STOPPED);
                        t.status = (int) TwinkleBeautyGroupModel.Status.STOPPED;
                    }
                }else{
                    //t.status = (int) TwinkleBeautyGroupModel.Status.RUNNING;
                }
            }
        }
        return ret;
    }
}
