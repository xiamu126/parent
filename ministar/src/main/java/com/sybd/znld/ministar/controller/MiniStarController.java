package com.sybd.znld.ministar.controller;

import com.mongodb.client.MongoClient;
import com.sybd.znld.mapper.lamp.LampMapper;
import com.sybd.znld.mapper.lamp.MiniStarEffectMapper;
import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.mapper.rbac.UserMapper;
import com.sybd.znld.ministar.Service.IOneNetService;
import com.sybd.znld.ministar.model.SubtitleForRegion;
import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.MiniStarEffectModel;
import com.sybd.znld.model.lamp.dto.MiniStarEffect;
import com.sybd.znld.model.lamp.dto.RegionWithLamps;
import com.sybd.znld.model.onenet.Command;
import com.sybd.znld.model.onenet.dto.CommandParams;
import com.sybd.znld.model.onenet.dto.OneNetExecuteArgs;
import com.sybd.znld.util.MyString;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "灯带接口")
@RestController
@RequestMapping("/api/v1/ministar")
public class MiniStarController {
    private final MongoClient mongoClient;
    private final RedissonClient redissonClient;
    private final OrganizationMapper organizationMapper;
    private final RegionMapper regionMapper;
    private final LampMapper lampMapper;
    private final MiniStarEffectMapper miniStarEffectMapper;
    private final UserMapper userMapper;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final IOneNetService oneNetService;

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
                              IOneNetService oneNetService) {
        this.mongoClient = mongoClient;
        this.redissonClient = redissonClient;
        this.organizationMapper = organizationMapper;
        this.regionMapper = regionMapper;
        this.lampMapper = lampMapper;
        this.miniStarEffectMapper = miniStarEffectMapper;
        this.userMapper = userMapper;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.oneNetService = oneNetService;
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

    @ApiOperation(value = "新建灯带效果，针对区域街道的")
    @PostMapping(value = "region", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public BaseApiResult newMiniStar(@ApiParam(value = "具体的指令集", required = true) @RequestBody SubtitleForRegion subtitle, HttpServletRequest request) {
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

    @ApiOperation(value = "新增（下发）灯带效果")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public void newMiniStar(){
       /* var subtitle = new SubtitleForRegion();
        subtitle.beginTime = LocalDateTime.now().plusMinutes(10);
        subtitle.endTime = LocalDateTime.now().plusMinutes(30);
        subtitle.regionId = "5aa2ac64883611e9a7fe0242c0a8b002";
        subtitle.colors = List.of(new SubtitleForRegion.Rgb(123,123,123),new SubtitleForRegion.Rgb(123,123,123),new SubtitleForRegion.Rgb(123,123,123));
        subtitle.speed = 10;
        subtitle.brightness = 20;
        subtitle.creatorId = "c9a45d5d972011e9b0790242c0a8b006";
        subtitle.type = SubtitleForRegion.Type.HX;

        var db = mongoClient.getDatabase( "test" );
        var c1 = db.getCollection("com.sybd.znld.ministar", SubtitleForRegion.class);
        var d1 = new Document();
        d1.append("twinkle_beauty", subtitle);
        c1.insertOne(subtitle);*/
    }

    @ApiOperation(value = "获取所有有效区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value="tree/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public List<RegionWithLamps> getAllRegionWithValidLamp(@PathVariable(name = "organId")String organId, HttpServletRequest request) {
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

    @ApiOperation(value = "保存效果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "effects", value = "新建效果集", required = true, dataType = "list", paramType = "body")
    })
    @PostMapping(value="effect/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public BaseApiResult saveEffect(@PathVariable(name = "organId")String organId,
                                    @RequestBody List<MiniStarEffect> effects, HttpServletRequest request){
        var result = new BaseApiResult();
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
        for(var e : effects){
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
        return result;
    }

    @ApiOperation(value = "获取效果列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organId", value = "组织Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value="effect/{organId:^[0-9a-f]{32}$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult getEffects(@PathVariable(name = "organId")String organId){
        var result = new ApiResult();
        if(!MyString.isUuid(organId)){
            result.code = 1;
            result.msg = "参数错误";
            return result;
        }
        var list = this.miniStarEffectMapper.selectByOrganId(organId);
        var data = list.stream().map(l -> {
            var tmp = new MiniStarEffect();
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
}
