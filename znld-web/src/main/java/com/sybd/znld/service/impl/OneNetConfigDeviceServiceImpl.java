package com.sybd.znld.service.impl;

import com.sybd.znld.config.ProjectConfig;
import com.sybd.znld.mapper.OneNetConfigDeviceMapper;
import com.sybd.znld.onenet.dto.OneNetKey;
import com.sybd.znld.service.OneNetConfigDeviceService;
import com.sybd.znld.service.RedisService;
import com.sybd.znld.service.dto.CheckedResource;
import com.sybd.znld.service.dto.DeviceIdAndDeviceName;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Slf4j
@Service
public class OneNetConfigDeviceServiceImpl extends BaseServiceImpl implements OneNetConfigDeviceService {

    private final OneNetConfigDeviceMapper onenetConfigDeviceMapper;
    private final RedisService redisService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public OneNetConfigDeviceServiceImpl(OneNetConfigDeviceMapper onenetConfigDeviceMapper,
                                         RedisService redisService,
                                         ProjectConfig projectConfig,
                                         CacheManager cacheManager,
                                         TaskScheduler taskScheduler) {
        super(cacheManager, taskScheduler, projectConfig);
        this.onenetConfigDeviceMapper = onenetConfigDeviceMapper;
        this.redisService = redisService;
    }

    @Override
    @Cacheable
    public Map<Integer, String> getDeviceIdAndImeis() {
        var list = this.onenetConfigDeviceMapper.getDeviceIdAndImeis();
        if(list == null || list.size() <= 0){
            return null;
        }
        var map = new HashMap<Integer, String>();
        for(var item : list){
            map.put(item.getDeviceId(), item.getImei());
        }
        return map;
    }

    @Override
    @Cacheable
    public List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames() {
        return this.onenetConfigDeviceMapper.getDeviceIdAndDeviceNames();
    }

    @Override
    public List<CheckedResource> getCheckedResources(Integer deviceId) {
        return this.onenetConfigDeviceMapper.getCheckedResources(deviceId);
    }

    @Override
    public boolean isDataStreamIdEnabled(String dataStreamId) {
        var oneNetKey = OneNetKey.from(dataStreamId);
        var ret =this.onenetConfigDeviceMapper.isDataStreamIdEnabled(oneNetKey);
        return ret != null && ret;
    }

    @Override
    @Cacheable(key="#root.targetClass.getName()+'.imei_'+#deviceId")
    public String getImeiByDeviceId(Integer deviceId) {
        //为了防止缓存穿透（指定的key在缓存中不存在时，不断访问、间接导致对数据库的频繁访问）
        //此方法简单粗暴，不管从数据库中有没有查到数据，每次访问数据库都缓存结果（包括空）
        var ret = this.onenetConfigDeviceMapper.getImeiByDeviceId(deviceId);
        if(ret == null){
            this.removeCache(this.getClass(),".imei_"+deviceId, expirationTime);
        }
        return ret;
    }

    @Override
    @Cacheable
    public String getDescBy(Integer objId, Integer objInstId, Integer resId) {
        return this.onenetConfigDeviceMapper.getDescBy(objId, objInstId, resId);
    }

    @Override
    @Cacheable
    public List<Integer> getDeviceIds() {
        return this.onenetConfigDeviceMapper.getDeviceIds();
    }

    @Override
    @Cacheable
    public Map<Integer, String> getDeviceIdNameMap() {
        var list = this.onenetConfigDeviceMapper.getDeviceIdNames();
        var map = new HashMap<Integer, String>();
        for(var item : list){
            map.put(item.getId(), item.getName());
        }
        return map;
    }

    @Override
    @Cacheable
    public String getApiKeyByDeviceId(Integer deviceId) {
        return this.onenetConfigDeviceMapper.getApiKeyByDeviceId(deviceId);
    }

    @Override
    @Cacheable
    public String getOneNetKey(String name) {
        return this.onenetConfigDeviceMapper.getOneNetKey(name);
    }

    @Override
    @Cacheable
    public Map<String, String> getInstanceMap(Integer deviceId) {
        var list = this.onenetConfigDeviceMapper.getInstanceMap(deviceId);
        var map = new HashMap<String, String>();
        for(var item : list){
            map.put(item.getName(), item.getOneNetKey());
        }
        return map;
    }
}
