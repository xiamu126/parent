package com.sybd.znld.service;


import java.util.List;
import java.util.concurrent.TimeUnit;

public interface RedisService {
    void set(String key, Object value);
    void set(String key, Object value, long timeout, TimeUnit timeUnit);
    Object get(String key);
    Boolean exists(String key);
    List<String> scan(String pattern);
    Boolean delete(String key);
}
