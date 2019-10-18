package com.sybd.znld.ministar.Service;

import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.ministar.model.SubtitleForDevice;
import com.sybd.znld.ministar.model.SubtitleForRegion;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.model.onenet.dto.CommandParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
public class MiniStarService implements IMiniStarService {
    private final UserMapper userMapper;
    private final RegionMapper regionMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final LampMapper lampMapper;
    private final IOneNetService oneNetService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MiniStarService(UserMapper userMapper, RegionMapper regionMapper, OneNetResourceMapper oneNetResourceMapper, LampMapper lampMapper, IOneNetService oneNetService) {
        this.userMapper = userMapper;
        this.regionMapper = regionMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.lampMapper = lampMapper;
        this.oneNetService = oneNetService;
    }

    @Override
    public BaseApiResult newMiniStar(SubtitleForRegion subtitle) {
        var result = new BaseApiResult();
        if(subtitle == null || !subtitle.isValid()){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var obj = this.regionMapper.selectByRegionIdAndUserId(subtitle.regionId, subtitle.userId);
        if(obj == null){
            log.debug("指定的区域和账号无关");
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var user = this.userMapper.selectById(subtitle.userId);
        if(user == null) {
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var region = this.regionMapper.selectById(subtitle.regionId);
        if(region == null) {
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var resource = this.oneNetResourceMapper.selectByCommandValue(Command.ZNLD_DD_EXECUTE);
        if(resource == null) {
            log.error("获取景观灯下发资源发生错误");
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var lamps = this.lampMapper.selectByRegionId(subtitle.regionId);
        if(lamps == null || lamps.isEmpty()) {
            log.error("指定区域下未包含任何路灯");
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var map = new HashMap<Integer, BaseApiResult>();
        for (var lamp : lamps) {
            var params = new CommandParams();
            params.deviceId = lamp.deviceId;
            params.imei = lamp.imei;
            params.oneNetKey = resource.toOneNetKey();
            params.timeout = resource.timeout;
            params.command = subtitle.toString(); // "args":"1567003994,1567004001,1,50,2,FF00FF0000FF"
            var ret = oneNetService.execute(params);
            map.put(lamp.deviceId, new BaseApiResult(ret.errno, ret.error));
        }
        var errCount = 0;
        for(var r : map.entrySet()){
            if(!r.getValue().isOk()) errCount++;
        }
        result.code = 0;
        result.msg = "下发成功，其中有"+errCount+"盏返回失败代码";
        return result;
    }

    @Override
    public BaseApiResult newMiniStar(SubtitleForDevice subtitle) {
        var result = new BaseApiResult();
        if(subtitle == null || !subtitle.isValid()){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var user = this.userMapper.selectById(subtitle.userId);
        if(user == null) {
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var lamp = this.lampMapper.selectByDeviceId(subtitle.deviceId);
        if(lamp == null) {
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var resource = this.oneNetResourceMapper.selectByCommandValue(Command.ZNLD_DD_EXECUTE);
        if(resource == null) {
            log.error("获取景观灯下发资源发生错误");
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var params = new CommandParams();
        params.deviceId = lamp.deviceId;
        params.imei = lamp.imei;
        params.oneNetKey = resource.toOneNetKey();
        params.timeout = resource.timeout;
        params.command = subtitle.toString(); // "args":"1567003994,1567004001,1,50,2,FF00FF0000FF"
        var ret = oneNetService.execute(params);
        if(ret != null && ret.errno == 0){
            result.code = 0;
            result.msg = "下发成功";
            return result;
        }
        result.code = 1;
        result.msg = "下发失败";
        return result;
    }
}
