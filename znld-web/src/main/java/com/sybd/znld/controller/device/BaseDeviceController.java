package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.onenet.IOneNetService;
import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.service.rbac.IUserService;
import com.sybd.znld.service.znld.ILampService;
import org.springframework.data.redis.core.RedisTemplate;

public class BaseDeviceController{
    protected RedisTemplate<String, Object> redisTemplate;
    protected IOneNetService oneNet;
    protected ILampService lampService;
    protected ProjectConfig projectConfig;
    protected IUserService userService;

    public BaseDeviceController(RedisTemplate<String, Object> redisTemplate,
                                IOneNetService oneNet,
                                ILampService lampService,
                                ProjectConfig projectConfig,
                                IUserService userService) {
        this.redisTemplate = redisTemplate;
        this.oneNet = oneNet;
        this.lampService = lampService;
        this.projectConfig = projectConfig;
        this.userService = userService;
    }
}
