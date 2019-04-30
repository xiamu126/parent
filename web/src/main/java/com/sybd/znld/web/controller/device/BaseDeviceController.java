package com.sybd.znld.web.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.web.onenet.IOneNetService;
import com.sybd.znld.web.service.rbac.IUserService;
import com.sybd.znld.web.service.znld.ILampService;
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
