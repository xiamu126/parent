package com.sybd.znld.controller;

import com.sybd.znld.config.RedisKeyConfig;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.service.VideoService;
import com.sybd.znld.video.dto.VideoData;
import com.sybd.znld.service.RedisService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

@Api(tags = "视频接口")
@RestController
@RequestMapping("/api/v1/video")
public class VideoController {
    private final RedisService redisService;
    private final VideoService videoService;
    private final Logger log = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    public VideoController(RedisService redisService, VideoService videoService) {
        this.redisService = redisService;
        this.videoService = videoService;
    }

    @ApiOperation(value = "推送视频")
    @PostMapping(value = "play", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult play(@ApiParam(name = "jsonData", value = "视频相关数据", required = true) @RequestBody VideoData jsonData){
        var key = RedisKeyConfig.CLIENT_CHANNEL_GUID_PREFIX+jsonData.getChannelGuid();
        if(jsonData.getCmd().equals("push")){
            if(videoService.push(key, jsonData)){
                return ApiResult.success("推流成功");
            }
            return ApiResult.fail("推流失败");
        }

        if(videoService.stop(key, jsonData)){
            return ApiResult.success();
        }
        return ApiResult.fail("关闭失败");
    }

    @ApiOperation(value = "发送心跳，心跳停止，视频推流会自动结束")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelGuid", value = "频道Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "heartbeat/{channelGuid:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult heartbeat(@PathVariable(name = "channelGuid") String channelGuid){
        var key = RedisKeyConfig.CLIENT_CHANNEL_GUID_PREFIX+channelGuid;
        if(this.videoService.isChannelInUsing(channelGuid)){
            redisService.set(key,"", 30, TimeUnit.SECONDS);
            return ApiResult.success();
        }else {
            return ApiResult.fail("已经关闭");
        }
    }

    @ApiOperation(value = "获取视频截图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelGuid", value = "频道Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "image/{channelGuid:[0-9a-f]{32}}", produces = {MediaType.IMAGE_PNG_VALUE})
    public BufferedImage pickImage(@PathVariable(name = "channelGuid") String channelGuid){
        return this.videoService.pickImage(channelGuid);
    }
}
