package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.znld.mapper.ExecuteCommandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Service
public class ExecuteCommandServiceImpl extends BaseService {
    private final ExecuteCommandMapper executeCommandMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ExecuteCommandServiceImpl(ExecuteCommandMapper executeCommandMapper,
                                     CacheManager cacheManager,
                                     TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        super(cacheManager, taskScheduler, projectConfig);
        this.executeCommandMapper = executeCommandMapper;
    }
}
