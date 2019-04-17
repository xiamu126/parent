package com.sybd.znld.controller;

import com.sybd.znld.config.RedisKeyConfig;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.service.video.dto.VideoData;
import com.sybd.znld.service.znld.IVideoService;
import io.swagger.annotations.*;
import org.redisson.api.RedissonClient;
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
    private final RedissonClient redissonClient;
    private final IVideoService videoService;
    private final Logger log = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    public VideoController(RedissonClient redissonClient, IVideoService videoService) {
        this.redissonClient = redissonClient;
        this.videoService = videoService;
    }

    @ApiOperation(value = "推送视频")
    @PostMapping(value = "play", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult play(@ApiParam(name = "jsonData", value = "视频相关数据", required = true) @RequestBody VideoData jsonData){
        try{
            if(jsonData.cmd.equals("push")){
                if(videoService.push(jsonData.channelGuid)){
                    return ApiResult.success("推流成功");
                }
                return ApiResult.fail("推流失败");
            }
            videoService.stop(jsonData.channelGuid);
            return ApiResult.success();
        }catch (Exception ex){
            log.error(ex.getMessage());
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
        if(this.videoService.isChannelInUsing(channelGuid)){//这个是否如果有人把全部频道都关闭了
            redissonClient.getBucket(key).set("", 30, TimeUnit.SECONDS);
            return ApiResult.success();
        }else {
            return ApiResult.fail("频道已经关闭");
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
