package com.sybd.znld.onenet.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.lamp.ILampService;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.service.rbac.IUserService;
import org.redisson.api.RedissonClient;

public class BaseDeviceController{
    protected RedissonClient redissonClient;
    protected IOneNetService oneNet;
    protected ILampService lampService;
    protected ProjectConfig projectConfig;
    protected IUserService userService;

    public BaseDeviceController(RedissonClient redissonClient,
                                IOneNetService oneNet,
                                ILampService lampService,
                                ProjectConfig projectConfig,
                                IUserService userService) {
        this.redissonClient = redissonClient;
        this.oneNet = oneNet;
        this.lampService = lampService;
        this.projectConfig = projectConfig;
        this.userService = userService;
    }
}
