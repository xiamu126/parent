package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.onenet.BetterOneNetService;
import com.sybd.znld.service.ExecuteCommandService;
import org.springframework.data.redis.core.RedisTemplate;

public class BaseDeviceController {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final BetterOneNetService oneNet;
    protected final ExecuteCommandService executeCommandService;
    protected final ProjectConfig projectConfig;

    public BaseDeviceController(RedisTemplate<String, Object> redisTemplate,
                                BetterOneNetService oneNet,
                                ExecuteCommandService executeCommandService,
                                ProjectConfig projectConfig) {
        this.redisTemplate = redisTemplate;
        this.oneNet = oneNet;
        this.executeCommandService = executeCommandService;
        this.projectConfig = projectConfig;
    }
}
