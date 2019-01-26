package com.sybd.znld.controller.device;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.onenet.OneNetService;
import com.sybd.znld.service.ExecuteCommandService;
import org.springframework.data.redis.core.RedisTemplate;

open class BaseDeviceController constructor(@JvmField protected val redisTemplate: RedisTemplate<String, Any>,
                                            @JvmField protected val oneNet: OneNetService,
                                            @JvmField protected val executeCommandService: ExecuteCommandService,
                                            @JvmField protected val projectConfig: ProjectConfig)
