package com.sybd.znld.ministar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import com.sybd.znld.ministar.model.Subtitle;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Api(tags = "灯带接口")
@RestController
@RequestMapping("/api/v1/ministar")
public class MiniStarController {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private  RedissonClient redissonClient;


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
}
