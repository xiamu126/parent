package com.sybd.znld.service;

import java.time.Duration;

public interface BaseService {
    void removeAllCache();
    void removeCache(Class clazz, String suffix, Duration expirationTime);
    Duration getExpirationTime();
}
