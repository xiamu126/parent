package com.sybd.znld.ministar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import com.sybd.znld.mapper.lamp.RegionMapper;
import com.sybd.znld.mapper.rbac.OrganizationMapper;
import com.sybd.znld.ministar.model.Subtitle;
import com.sybd.znld.model.lamp.dto.Lamp;
import com.sybd.znld.model.lamp.dto.Region;
import com.sybd.znld.model.lamp.dto.RegionWithLamps;
import com.sybd.znld.util.MyString;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MiniStarController(MongoClient mongoClient, RedissonClient redissonClient, OrganizationMapper organizationMapper, RegionMapper regionMapper) {
        this.mongoClient = mongoClient;
        this.redissonClient = redissonClient;
        this.organizationMapper = organizationMapper;
        this.regionMapper = regionMapper;
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

    @ApiOperation(value = "新增（下发）灯带效果")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public void newMiniStar(){
        var subtitle = new Subtitle();
        subtitle.beginTime = LocalDateTime.now().plusMinutes(10);
        subtitle.endTime = LocalDateTime.now().plusMinutes(30);
        subtitle.regionId = "5aa2ac64883611e9a7fe0242c0a8b002";
        subtitle.colors = List.of(new Subtitle.Rgb(123,123,123),new Subtitle.Rgb(123,123,123),new Subtitle.Rgb(123,123,123));
        subtitle.speed = 10;
        subtitle.brightness = 20;
        subtitle.creatorId = "c9a45d5d972011e9b0790242c0a8b006";
        subtitle.type = Subtitle.Type.HX;

        var db = mongoClient.getDatabase( "test" );
        var c1 = db.getCollection("com.sybd.znld.ministar", Subtitle.class);
        var d1 = new Document();
        d1.append("twinkle_beauty", subtitle);
        c1.insertOne(subtitle);
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
            var lamps = this.regionMapper.selectLampsByRegionId(r.regionId);
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
}
