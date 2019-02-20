package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.service.ExecuteCommandService;
import org.springframework.data.redis.core.RedisTemplate;

public class BaseDeviceController{
    protected RedisTemplate<String, Object> redisTemplate;
    protected  OneNetService oneNet;
    protected ExecuteCommandService executeCommandService;
    protected ProjectConfig projectConfig;

    public BaseDeviceController(RedisTemplate<String, Object> redisTemplate, OneNetService oneNet, ExecuteCommandService executeCommandService, ProjectConfig projectConfig) {
        this.redisTemplate = redisTemplate;
        this.oneNet = oneNet;
        this.executeCommandService = executeCommandService;
        this.projectConfig = projectConfig;
    }
}
