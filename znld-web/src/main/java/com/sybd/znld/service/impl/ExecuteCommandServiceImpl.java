package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.mapper.ExecuteCommandMapper;
import com.sybd.znld.service.model.ExecuteCommandEntity;
import com.sybd.znld.onenet.dto.OneNetExecuteParams;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.ExecuteCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
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
        ExecuteCommandEntity entity = this.executeCommandMapper.getByCommand(cmd);
        if(entity == null) return null;
        OneNetKey oneNetKey = new OneNetKey();
        oneNetKey.objId = entity.objId;
        oneNetKey.objInstId = entity.objInstId;
        oneNetKey.resId = entity.resId;
        return oneNetKey;
    }

    @Override
    @Cacheable(key="#root.targetClass.getName()+'.getParamsByCommand'+#cmd")
    public OneNetExecuteParams getParamsByCommand(String cmd, boolean evict){
        if(evict){
            this.removeCache(this.getClass(),".getParamsByCommand"+cmd);
            return null;
        }
        var entity = this.executeCommandMapper.getByCommand(cmd);
        if(entity == null) return null;
        var oneNetKey = getOneNetKeyByCommand(cmd);
        if(oneNetKey == null) return null;
        return new OneNetExecuteParams(oneNetKey, entity.getTimeout());
    }
}
