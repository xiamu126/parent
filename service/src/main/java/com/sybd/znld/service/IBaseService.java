package com.sybd.znld.service;

import java.time.Duration;

public interface IBaseService {
    void removeAllCache();
    void removeCache(Class clazz, String suffix, Duration expirationTime);
    void removeCache(Class clazz, String suffix);
    Duration getExpirationTime();
}
