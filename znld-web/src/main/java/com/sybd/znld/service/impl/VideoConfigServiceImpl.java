package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.mapper.VideoConfigMapper;
import com.sybd.znld.service.model.VideoConfigEntity;
import com.sybd.znld.service.VideoConfigServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class VideoConfigServiceImpl extends BaseService implements VideoConfigServiceI {
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
        int ret = videoConfigMapper.setConfigByCameraId(videoConfigEntity);
        return 0;
    }
}
