package com.sybd.znld.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.config.ProjectConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.scheduling.TaskScheduler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Slf4j
@CacheConfig(cacheNames={BaseService.cachePrefix})
public class BaseService implements IBaseService {
    protected static final String cachePrefix = "ZNLD::API::CACHE::";

    protected final CacheManager cacheManager;
    protected final TaskScheduler taskScheduler;
    protected final ProjectConfig projectConfig;
    protected static Duration nullExpirationTime;
    protected final RedissonClient redissonClient;
    protected final ObjectMapper objectMapper;

    protected static final boolean cacheMode = false;

    public BaseService(CacheManager cacheManager,
                       TaskScheduler taskScheduler,
                       ProjectConfig projectConfig,
                       RedissonClient redissonClient, ObjectMapper objectMapper) {
        this.cacheManager = cacheManager;
        this.taskScheduler = taskScheduler;
        this.projectConfig = projectConfig;
        this.redissonClient = redissonClient;
        this.objectMapper = objectMapper;

        Duration time = this.projectConfig.getCacheOfNullExpirationTime();
        if(time == null || time.isZero()){
            throw new RuntimeException("过期时间未设置");
        }else{
            nullExpirationTime = time;
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
        taskScheduler.schedule(
                () -> { _removeCache(clazz, suffix); },
                LocalDateTime.now().plus(expirationTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public void removeCache(Class clazz, String suffix) {
        _removeCache(clazz, suffix);
    }

    @Override
    public void putCache(String key, Object value) {
        if(!cacheMode) return;
        try {
            var tmp = this.objectMapper.writeValueAsString(value);
            this.redissonClient.getBucket(key).set(tmp);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void putNullCache(String key) {
        if(!cacheMode) return;
        this.redissonClient.getBucket(key).set("", nullExpirationTime.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void putCache(String key, Object value, Duration expirationTime) {
        if(!cacheMode) return;
        try{
            var tmp = this.objectMapper.writeValueAsString(value);
            this.redissonClient.getBucket(key).set(tmp, expirationTime.toSeconds(), TimeUnit.SECONDS);
        }catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public Object getCache(String key) {
        if(!cacheMode) return null;
        return this.redissonClient.getBucket(key).get();
    }

    @Override
    public <T> T getCache(String key, Class<T> clazz) {
        if(!cacheMode) return null;
        try {
            var tmp = this.redissonClient.getBucket(key).get();
            return this.objectMapper.readValue(tmp.toString(), clazz);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    private void _removeCache(Class clazz, String suffix) {
        var cache = this.cacheManager.getCache(cachePrefix);
        if(cache != null){
            cache.evict(clazz.getName()+suffix);
        }else{
            log.debug("获取cache失败");
        }
    }

    @Override
    public Duration getNullExpirationTime() {
        return nullExpirationTime;
    }

    @Override
    public String getCacheKey(String suffix) {
        return cachePrefix + suffix;
    }
}
