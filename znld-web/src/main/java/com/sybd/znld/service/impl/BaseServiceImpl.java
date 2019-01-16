package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@CacheConfig(cacheNames={BaseServiceImpl.cachePrefix})
public class BaseServiceImpl implements BaseService {
    protected static final String cachePrefix = "znld::web::service::cache";

    protected final CacheManager cacheManager;
    protected final TaskScheduler taskScheduler;
    protected final ProjectConfig projectConfig;
    protected static Duration expirationTime;

    public BaseServiceImpl(CacheManager cacheManager, TaskScheduler taskScheduler, ProjectConfig projectConfig) {
        this.cacheManager = cacheManager;
        this.taskScheduler = taskScheduler;
        this.projectConfig = projectConfig;

        var time = this.projectConfig.getCacheOfNullExpirationTime();
        if(time == null || time.isZero()){
            throw new RuntimeException("过期时间未设置");
        }else{
            expirationTime = time;
        }
    }

    @Override
    public void removeAllCache(){
        var cache = this.cacheManager.getCache(cachePrefix);
        if(cache != null){
            cache.clear();
        }else{
            log.debug("获取cache失败");
        }
    }

    @Override
    public void removeCache(Class clazz, String suffix, Duration expirationTime) {
        taskScheduler.schedule(()->{
            var cache = this.cacheManager.getCache(cachePrefix);
            if(cache != null){
                cache.evict(clazz.getName()+suffix);
            }else{
                log.debug("获取cache失败");
            }
        }, LocalDateTime.now().plus(expirationTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Duration getExpirationTime() {
        return expirationTime;
    }
}
