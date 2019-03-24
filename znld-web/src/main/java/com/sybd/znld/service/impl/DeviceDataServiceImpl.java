package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.DeviceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Service
public class DeviceDataServiceImpl extends BaseService implements DeviceDataService {
    @Autowired
    public DeviceDataServiceImpl(CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        super(cacheManager, taskScheduler, projectConfig);
    }
}
