package com.sybd.znld.ministar.controller;

import com.mongodb.client.MongoClient;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.ministar.Service.IMiniStarService;
import com.sybd.znld.ministar.Service.IOneNetService;
import com.sybd.znld.ministar.model.*;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.MiniStarEffectModel;
import com.sybd.znld.model.lamp.MiniStarTaskModel;
import com.sybd.znld.model.lamp.dto.MiniStarEffect;
import com.sybd.znld.model.lamp.dto.MiniStarEffectForSave;
import com.sybd.znld.model.lamp.dto.RegionWithLamps;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "灯带接口")
@RestController
@RequestMapping("/api/v1/ministar")
public class MiniStarController implements IMiniStarController{
    private final MongoClient mongoClient;
    private final RedissonClient redissonClient;
    private final OrganizationMapper organizationMapper;
    private final RegionMapper regionMapper;
    private final LampMapper lampMapper;
    private final MiniStarEffectMapper miniStarEffectMapper;
    private final UserMapper userMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final IOneNetService oneNetService;
    private final MiniStarTaskMapper miniStarTaskMapper;
    private final IMiniStarService miniStarService;

    private static final List<String> TYPES = List.of("呼吸灯", "跑马灯", "闪烁灯");

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MiniStarController(MongoClient mongoClient,
                              RedissonClient redissonClient,
                              OrganizationMapper organizationMapper,
                              RegionMapper regionMapper,
                              LampMapper lampMapper,
                              MiniStarEffectMapper miniStarEffectMapper,
                              UserMapper userMapper,
                              OneNetResourceMapper oneNetResourceMapper,
                              IOneNetService oneNetService, MiniStarTaskMapper miniStarTaskMapper, IMiniStarService miniStarService) {
        this.mongoClient = mongoClient;
        this.redissonClient = redissonClient;
        this.organizationMapper = organizationMapper;
        this.regionMapper = regionMapper;
        this.lampMapper = lampMapper;
        this.miniStarEffectMapper = miniStarEffectMapper;
        this.userMapper = userMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.oneNetService = oneNetService;
        this.miniStarTaskMapper = miniStarTaskMapper;
        this.miniStarService = miniStarService;
    }

    @ApiOperation(value = "获取全部的灯带下发历史")
    @GetMapping(value = "history", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public void getHistory(){
        var db = mongoClient.getDatabase( "test" );
        var c1 = db.getCollection("com.sybd.znld.ministar");
        var myDoc = c1.find();
        for (org.bson.Document d : myDoc) {
            System.out.println(d.toJson());
        }
    }

    @Override
    public ApiResult storeTask(String organId, SubtitleForRegionPrepare subtitles, HttpServletRequest request) {
        var result = new ApiResult();
        if(!MyString.isUuid(organId) || subtitles == null || !subtitles.isValid()){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var tmp = this.regionMapper.selectByRegionIdAndOrganId(subtitles.regionId, organId);
        if(tmp == null){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var user = this.userMapper.selectById(subtitles.userId);
        if(user == null){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        for(var time : subtitles.times){
            var model = new MiniStarTaskModel();
            model.targetId = subtitles.regionId;
            model.targetType = MiniStarTaskModel.TargetType.REGION;
            model.userId = subtitles.userId;
            model.organizationId = organId;
            model.status = MiniStarTaskModel.TaskStatus.WAITING;
            model.beginTime = MyDateTime.toLocalDateTime(time.beginTimestamp);
            model.endTime = MyDateTime.toLocalDateTime(time.endTimestamp);
            model.effectType = subtitles.getTypeName();
            model.colors = subtitles.getColorsString();
            model.speed = subtitles.speed;
            model.brightness = subtitles.brightness;
            model.title = subtitles.title;

            var tmp2 = new SubtitleForRegion();
            tmp2.title = subtitles.title;
            tmp2.userId = subtitles.userId;
            tmp2.regionId = subtitles.regionId;
            tmp2.type = subtitles.type;
            tmp2.speed = subtitles.speed;
            tmp2.brightness = subtitles.brightness;
            tmp2.colors = subtitles.colors;
            tmp2.beginTimestamp = time.beginTimestamp;
            tmp2.endTimestamp = time.endTimestamp;

            model.cmd = tmp2.toString();
            this.miniStarTaskMapper.insert(model);
        }
        result.code = 0;
        result.msg = "";
        result.data = subtitles.times.size();
        return result;
    }

    @Override
    public ApiResult storeTaskForDevice(String organId, SubtitleForDevicePrepare subtitles, HttpServletRequest request) {
        var result = new ApiResult();
        if(!MyString.isUuid(organId) || subtitles == null || !subtitles.isValid()){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var device = this.lampMapper.selectByDeviceId(subtitles.deviceId);
        if(device == null) {
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var user = this.userMapper.selectById(subtitles.userId);
        if(user == null){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        for(var time : subtitles.times){
            var model = new MiniStarTaskModel();
            model.targetId = subtitles.deviceId.toString();
            model.targetType = MiniStarTaskModel.TargetType.DEVICE;
            model.userId = subtitles.userId;
            model.organizationId = organId;
            model.status = MiniStarTaskModel.TaskStatus.WAITING;
            model.beginTime = MyDateTime.toLocalDateTime(time.beginTimestamp);
            model.endTime = MyDateTime.toLocalDateTime(time.endTimestamp);
            model.effectType = subtitles.getTypeName();
            model.colors = subtitles.getColorsString();
            model.speed = subtitles.speed;
            model.brightness = subtitles.brightness;
            model.title = subtitles.title;

            var tmp2 = new SubtitleForDevice();
            tmp2.title = subtitles.title;
            tmp2.userId = subtitles.userId;
            tmp2.deviceId = subtitles.deviceId;
            tmp2.type = subtitles.type;
            tmp2.speed = subtitles.speed;
            tmp2.brightness = subtitles.brightness;
            tmp2.colors = subtitles.colors;
            tmp2.beginTimestamp = time.beginTimestamp;
            tmp2.endTimestamp = time.endTimestamp;

            model.cmd = tmp2.toString();
            this.miniStarTaskMapper.insert(model);
        }
        result.code = 0;
        result.msg = "";
        result.data = subtitles.times.size();
        return result;
    }

    @Override
    public BaseApiResult newMiniStarForRegion(SubtitleForRegion subtitle, HttpServletRequest request) {
        return this.miniStarService.newMiniStar(subtitle);
    }

    @Override
    public BaseApiResult newMiniStarForDevice(SubtitleForDevice subtitle, HttpServletRequest request) {
        return this.miniStarService.newMiniStar(subtitle);
    }

    @Override
    public List<RegionWithLamps> getAllRegionWithValidLamp(String organId, HttpServletRequest request) {
        if(!MyString.isUuid(organId)){
            log.debug("非法的组织id");
            return null;
        }
        var organ = this.organizationMapper.selectById(organId);
        if(organ == null){
            log.debug("指定的组织不存在");
            return null;
        }
        var regions = this.regionMapper.selectByOrganId(organId);
        if(regions == null || regions.isEmpty()){
            log.debug("指定组织未关联任何区域");
            return null;
        }
        var result = new ArrayList<RegionWithLamps>();
        regions.forEach(r -> {
            var lamps = this.regionMapper.selectLampsWithLocationByRegionId(r.regionId);
            if(lamps != null && !lamps.isEmpty()){
                var tmp = new RegionWithLamps();
                tmp.regionId = r.regionId;
                tmp.regionName = r.regionName;
                tmp.lamps = lamps;
                result.add(tmp);
            }
        });
        return result;
    }

    public ApiResult saveEffects(String organId, List<MiniStarEffectForSave> effects, HttpServletRequest request){
        var result = new ApiResult();
        if(!MyString.isUuid(organId) || effects == null || effects.isEmpty()){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
        for(var e : effects){
            var bindingResult = new BindException(e, "e");
            validator.validate(e, bindingResult);
            if(bindingResult.hasErrors()){
                log.debug(bindingResult.getMessage());
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            if(e.colors.toCharArray().length % 6 != 0){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            if(!TYPES.contains(e.type)){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
        }
        var organ = this.organizationMapper.selectById(organId);
        if(organ == null){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var errCount = 0;
        for(var e : effects){
            var tmp = this.miniStarEffectMapper.selectByOrganIdAndName(organId, e.name);
            if(tmp != null){
                errCount++;
                continue;
            }
            var model = new MiniStarEffectModel();
            model.name = e.name;
            model.type = e.type;
            model.colors = e.colors;
            model.speed = e.speed;
            model.brightness = e.brightness;
            model.organizationId = organId;
            this.miniStarEffectMapper.insert(model);
        }
        result.code = 0;
        result.msg = "";
        result.data = effects.size() - errCount;
        return result;
    }

    @Override
    public ApiResult modifyEffects(String organId, List<MiniStarEffect> effects, HttpServletRequest request) {
        var result = new ApiResult();
        if(!MyString.isUuid(organId) || effects == null || effects.isEmpty()){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        for(var e : effects){
            if(!MyNumber.isPositive(e.id)){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            if(e.speed != null && e.speed <= 0){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            if(e.brightness != null && e.brightness < 0){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            if(e.type != null && !Subtitle.Type.isValid(e.type)){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            if(e.colors != null){
                if(!Subtitle.Rgb.isValid(e.colors)){
                    result.code = 1;
                    result.msg = "参数错误";
                    return result;
                }
            }
            if(e.name != null && e.name.equals("")){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
            if(e.name == null && e.brightness == null && e.speed == null && e.colors == null && e.type == null){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
        }
        var organ = this.organizationMapper.selectById(organId);
        if(organ == null){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var errCount = 0;
        for(var e : effects){
            var tmp = this.miniStarEffectMapper.selectByOrganIdAndName(organId, e.name);
            if(tmp == null){
                errCount++;
                continue;
            }
            var model = new MiniStarEffectModel();
            model.id = e.id;
            model.name = e.name;
            model.type = e.type;
            model.colors = e.colors;
            model.speed = e.speed;
            model.brightness = e.brightness;
            model.organizationId = organId;
            this.miniStarEffectMapper.update(model);
        }
        result.code = 0;
        result.msg = "";
        result.data = effects.size() - errCount;
        return result;
    }

    public ApiResult getEffects(String organId){
        var result = new ApiResult();
        if(!MyString.isUuid(organId)){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var list = this.miniStarEffectMapper.selectByOrganId(organId);
        var data = list.stream().map(l -> {
            var tmp = new MiniStarEffect();
            tmp.id = l.id;
            tmp.name = l.name;
            tmp.type = l.type;
            tmp.colors = l.colors;
            tmp.speed = l.speed;
            tmp.brightness = l.brightness;
            return tmp;
        });
        result.code = 0;
        result.msg = "";
        result.data = data;
        return result;
    }

    @Override
    public ApiResult removeEffects(String organId, Map<String, List<Integer>> data, HttpServletRequest request) {
        var result = new ApiResult();
        var effectIds = data.get("effectIds");
        if(!MyString.isUuid(organId) || effectIds == null || effectIds.isEmpty()){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        for(var id : effectIds){
            if(!MyNumber.isPositive(id)){
                result.code = 1;
                result.msg = "参数错误";
                return result;
            }
        }
        var ret = this.miniStarEffectMapper.deleteByIds(organId, effectIds);
        result.code = 0;
        result.msg = "";
        result.data = ret;
        return result;
    }
}
