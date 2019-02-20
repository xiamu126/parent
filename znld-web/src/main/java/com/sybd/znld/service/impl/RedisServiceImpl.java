package com.sybd.znld.service.impl;

import com.sybd.znld.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String LOCK_KEY = "znld_redis_lock_key";

    @Autowired
    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public Object get(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean exists(String key) {
        Boolean tmp = this.redisTemplate.hasKey(key);
        return tmp != null && tmp;
    }

    @Override
    //@SuppressWarnings("unchecked")
    public List<String> scan(String pattern){
       /* var options = ScanOptions.scanOptions().match(pattern).count(Integer.MAX_VALUE).build();
        var redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        return redisTemplate.executeWithStickyConnection(//这里使用executeWithStickyConnection，因为SCAN需要在同一条连接上执行。
                redisConnection -> new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize)
        );*/
        return redisTemplate.execute((RedisCallback<List<String>>) connection -> {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(Integer.MAX_VALUE).build();
            ArrayList<String> binaryKeys = new ArrayList<>();
            Cursor<byte[]> cursor = connection.scan(options);
            while (cursor.hasNext()) {
                String strKey = new String(cursor.next(), Charset.forName("UTF-8"));
                binaryKeys.add(strKey);
            }
            try {
                cursor.close();
            } catch (IOException e) {
                // do something meaningful
            }
            return binaryKeys;
        });
    }

    @Override
    public Boolean delete(String key) {
        return this.redisTemplate.delete(key);
    }

    public void get(){
        Object tmp = this.redisTemplate.opsForValue().get("");
        if(tmp == null){
            Boolean ret = this.redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, 1);
            if(ret == null) return;
            if(ret){
                this.redisTemplate.expire(LOCK_KEY, 1, TimeUnit.MINUTES);
                //缓存中已经没有数据，因为从db中获取
                this.redisTemplate.opsForValue().set("key", "cachePrefix", 10, TimeUnit.MINUTES);
                this.redisTemplate.delete(LOCK_KEY);
            }else{
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                get();
            }
        }
    }
}
