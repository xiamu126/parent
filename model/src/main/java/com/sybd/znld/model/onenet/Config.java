package com.sybd.znld.model.onenet;

public class Config {
    public static final String REDIS_REALTIME_PREFIX = "com.sybd.znld.onenet.realtime.";
    public static String getRedisRealtimeKey(String imei) {
        return REDIS_REALTIME_PREFIX + imei;
    }
}
