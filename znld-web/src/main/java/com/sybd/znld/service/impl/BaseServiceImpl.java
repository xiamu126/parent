package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@CacheConfig(cacheNames={BaseServiceImpl.cachePrefix})
public class BaseServiceImpl implements BaseService {
    private final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);
    protected static final String cachePrefix = "znld::web::service::cache";

    protected final CacheManager cacheManager;
    protected final TaskScheduler taskScheduler;
    protected final ProjectConfig projectConfig;
    protected static Duration expirationTime;

    public BaseServiceImpl(CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        this.cacheManager = cacheManager;
        this.taskScheduler = taskScheduler;
        this.projectConfig = projectConfig;

        Duration time = this.projectConfig.getCacheOfNullExpirationTime();
        if(time == null || time.isZero()){
            throw new RuntimeException("过期时间未设置");
        }else{
            expirationTime = time;
        }
    }

    @Override
    public void removeAllCache(){
        Cache cache = this.cacheManager.getCache(cachePrefix);
        if(cache != null){
            cache.clear();
        }else{
            log.debug("获取cache失败");
        }
    }

    @Override
    public void removeCache(Class clazz, String suffix, Duration expirationTime) {
        taskScheduler.schedule(()->{
            _removeCache(clazz, suffix);
        }, LocalDateTime.now().plus(expirationTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public void removeCache(Class clazz, String suffix) {
        _removeCache(clazz, suffix);
    }

    private void _removeCache(Class clazz, String suffix) {
        Cache cache = this.cacheManager.getCache(cachePrefix);
        if(cache != null){
            cache.evict(clazz.getName()+suffix);
        }else{
            log.debug("获取cache失败");
        }
    }

    @Override
    public Duration getExpirationTime() {
        return expirationTime;
    }
}
