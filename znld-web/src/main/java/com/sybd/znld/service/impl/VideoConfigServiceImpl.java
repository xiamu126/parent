package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.mapper.VideoConfigMapper;
import com.sybd.znld.model.VideoConfigEntity;
import com.sybd.znld.service.VideoConfigService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VideoConfigServiceImpl extends BaseServiceImpl implements VideoConfigService {
    private final VideoConfigMapper videoConfigMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public VideoConfigServiceImpl(VideoConfigMapper videoConfigMapper,
                                  CacheManager cacheManager,
                                  TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        super(cacheManager, taskScheduler, projectConfig);
        this.videoConfigMapper = videoConfigMapper;
    }

    @Override
    public VideoConfigEntity getConfigByCameraId(String cameraId) {
        return this.videoConfigMapper.getConfigByCameraId(cameraId);
    }

    @Override
    public int setConfigByCameraId(VideoConfigEntity videoConfigEntity) {
        var ret = videoConfigMapper.setConfigByCameraId(videoConfigEntity);
        return 0;
    }
}