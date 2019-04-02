package com.sybd.znld.service.impl;

import org.springframework.stereotype.Service;

@SuppressWarnings("SpringCacheNamesInspection")//在基类中已经设置了CacheConfig
@Service
public class OneNetConfigDeviceServiceImpl {

    /*private final OneNetConfigDeviceMapper onenetConfigDeviceMapper;
    private final RedissonClient redissonClient;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public OneNetConfigDeviceServiceImpl(OneNetConfigDeviceMapper onenetConfigDeviceMapper,
                                         RedissonClient redissonClient,
                                         ProjectConfig projectConfig,
                                         CacheManager cacheManager,
                                         TaskScheduler taskScheduler) {
        super(cacheManager, taskScheduler, projectConfig);
        this.onenetConfigDeviceMapper = onenetConfigDeviceMapper;
        this.redissonClient = redissonClient;
    }

    @Cacheable
    public Map<Integer, String> getDeviceIdAndIMEI() {
        List<DeviceIdAndImei> list = this.onenetConfigDeviceMapper.getDeviceIdAndIMEI();
        if(list == null || list.size() <= 0){
            return null;
        }
        HashMap<Integer, String> map = new HashMap<>();
        for(DeviceIdAndImei item : list){
            map.put(item.deviceId, item.imei);
        }
        return map;
    }

    @Cacheable
    public List<DeviceIdAndDeviceName> getDeviceIdAndDeviceNames() {
        return this.onenetConfigDeviceMapper.getDeviceIdAndDeviceNames();
    }

    public List<CheckedResource> getCheckedResources(Integer deviceId) {
        return this.onenetConfigDeviceMapper.getCheckedResources(deviceId);
    }

    public boolean isDataStreamIdEnabled(String dataStreamId) {
        OneNetKey dataStreamId = OneNetKey.from(dataStreamId);
        Boolean ret =this.onenetConfigDeviceMapper.isDataStreamIdEnabled(dataStreamId);
        return ret != null && ret;
    }

    @Cacheable(key="#root.targetClass.getName()+'.imei_'+#deviceId")
    public String getImeiByDeviceId(Integer deviceId) {
        //为了防止缓存穿透（指定的key在缓存中不存在时，不断访问、间接导致对数据库的频繁访问）
        //此方法简单粗暴，不管从数据库中有没有查到数据，每次访问数据库都缓存结果（包括空）
        String ret = this.onenetConfigDeviceMapper.getImeiByDeviceId(deviceId);
        if(ret == null){
            this.removeCache(this.getClass(),".imei_"+deviceId, expirationTime);
        }
        return ret;
    }

    @Cacheable
    public String getDescBy(Integer objId, Integer objInstId, Integer resId) {
        return this.onenetConfigDeviceMapper.getDescBy(objId, objInstId, resId);
    }

    @Cacheable
    public List<Integer> getDeviceIds() {
        return this.onenetConfigDeviceMapper.getDeviceIds();
    }

    @Cacheable
    public Map<Integer, String> getDeviceIdNameMap() {
        List<DeviceIdName> list = this.onenetConfigDeviceMapper.getDeviceIdNames();
        HashMap<Integer, String> map = new HashMap<>();
        for(DeviceIdName item : list){
            map.put(item.id, item.name);
        }
        return map;
    }

    @Cacheable
    public String getApiKeyByDeviceId(Integer deviceId) {
        return this.onenetConfigDeviceMapper.getApiKeyByDeviceId(deviceId);
    }

    @Cacheable
    public String getDataStreamId(String name) {
        return this.onenetConfigDeviceMapper.getDataStreamId(name);
    }

    @Cacheable
    public Map<String, String> getInstanceMap(Integer deviceId) {
        List<NameAndOneNetKey> list = this.onenetConfigDeviceMapper.getInstanceMap(deviceId);
        HashMap<String, String> map = new HashMap<>();
        for(NameAndOneNetKey item : list){
            map.put(item.name, item.dataStreamId);
        }
        return map;
    }*/
}
