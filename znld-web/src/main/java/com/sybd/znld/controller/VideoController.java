package com.sybd.znld.controller;

import com.sybd.znld.config.RedisKeyConfig;
import com.sybd.znld.core.ApiResult;
import com.sybd.znld.service.video.dto.VideoData;
import com.sybd.znld.service.znld.ILampService;
import com.sybd.znld.service.znld.IVideoService;
import com.sybd.znld.service.znld.mapper.LampMapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "视频接口")
@RestController
@RequestMapping("/api/v1/video")
public class VideoController {
    private final RedissonClient redissonClient;
    private final IVideoService videoService;
    private final ILampService lampService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public VideoController(RedissonClient redissonClient, IVideoService videoService, ILampService lampService) {
        this.redissonClient = redissonClient;
        this.videoService = videoService;
        this.lampService = lampService;
    }


    @ApiOperation(value = "获取首页视频相关数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelGuid", value = "频道Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "info/{deviceId:^[1-9]\\d*$}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult getVideoInfo(@PathVariable("deviceId") Integer deviceId){
        var ret = this.lampService.getActiveCameraByDeviceId(deviceId);
        if(ret != null){
            return ApiResult.success(ret);
        }
        return ApiResult.fail("获取失败");
    }

    @ApiOperation(value = "推送视频")
    @PostMapping(value = "play", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult play(@ApiParam(name = "jsonData", value = "视频相关数据", required = true) @RequestBody VideoData jsonData){
        if(jsonData == null || !jsonData.isValid()) return ApiResult.fail("错误的参数");
        try{
            if(jsonData.cmd.equals("push")){
                if(videoService.push(jsonData.channelGuid)){
                    return ApiResult.success("推流成功");
                }
                return ApiResult.fail("推流失败");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
        return ApiResult.fail("发生错误");
    }

    @ApiOperation(value = "发送心跳，心跳停止，视频推流会自动结束")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelGuid", value = "频道Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "heartbeat/{channelGuid:[0-9a-f]{32}}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ApiResult heartbeat(@PathVariable(name = "channelGuid") String channelGuid){
        if(this.videoService.heartbeat(channelGuid)){
            return ApiResult.success();
        }
        return ApiResult.fail("频道已经关闭");
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
