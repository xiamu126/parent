package com.sybd.znld.service.znld;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.db.DbSource;
import com.sybd.znld.model.znld.VideoConfigModel;
import com.sybd.znld.service.BaseService;
import com.sybd.znld.service.znld.mapper.VideoConfigMapper;
import com.whatever.util.MyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
@DbSource("znld")
public class VideoConfigService extends BaseService implements IVideoConfigService {
    private final Logger log = LoggerFactory.getLogger(VideoConfigService.class);

    private final VideoConfigMapper videoConfigMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public VideoConfigService(VideoConfigMapper videoConfigMapper,
                              CacheManager cacheManager,
                              TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        super(cacheManager, taskScheduler, projectConfig);
        this.videoConfigMapper = videoConfigMapper;
    }

    @Override
    public VideoConfigModel getConfigByCameraId(String cameraId) {
        if(!MyString.isUuid(cameraId)) return null;
        return this.videoConfigMapper.getConfigByCameraId(cameraId);
    }

    @Override
    public VideoConfigModel setConfigByCameraId(VideoConfigModel model) {
        if(model == null || !MyString.isUuid(model.id)) return null;
        if(videoConfigMapper.setConfigByCameraId(model) > 0) return model;
        return null;
    }
}
