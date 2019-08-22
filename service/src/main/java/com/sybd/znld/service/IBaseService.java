package com.sybd.znld.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;

public interface IBaseService {
    void removeAllCache();
    void removeCache(Class clazz, String suffix, Duration expirationTime);
    void removeCache(Class clazz, String suffix);
    void putCache(String key, Object value);
    void putNullCache(String key);
    void putCache(String key, Object value, Duration expirationTime);
    Object getCache(String key);
    <T> T getCache(String key, Class<T> clazz);
    Duration getNullExpirationTime();
    String getCacheKey(String suffix);
}
