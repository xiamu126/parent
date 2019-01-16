package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.mapper.ExecuteCommandMapper;
import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.ExecuteCommandService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Slf4j
@Service
public class ExecuteCommandServiceImpl extends BaseServiceImpl implements ExecuteCommandService {
    private final ExecuteCommandMapper executeCommandMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ExecuteCommandServiceImpl(ExecuteCommandMapper executeCommandMapper,
                                     CacheManager cacheManager,
                                     TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        super(cacheManager, taskScheduler, projectConfig);
        this.executeCommandMapper = executeCommandMapper;
    }

    @Override
    @Cacheable
    public OneNetKey getOneNetKeyByCommand(String cmd) {
        var entity = this.executeCommandMapper.getByCommand(cmd);
        if(entity == null) return null;
        var oneNetKey = new OneNetKey();
        oneNetKey.setObjId(entity.getObjId());
        oneNetKey.setObjInstId(entity.getObjInstId());
        oneNetKey.setResId(entity.getResId());
        return oneNetKey;
    }

    @Override
    @Cacheable
    public OneNetExecuteParams getParamsByCommand(String cmd){
        var entity = this.executeCommandMapper.getByCommand(cmd);
        if(entity == null) return null;
        var oneNetKey = getOneNetKeyByCommand(cmd);
        if(oneNetKey == null) return null;
        return new OneNetExecuteParams(oneNetKey, entity.getTimeout());
    }
}
