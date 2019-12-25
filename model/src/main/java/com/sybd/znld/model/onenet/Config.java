package com.sybd.znld.model.onenet;

public class Config {
    public static final String REDIS_REALTIME_PREFIX = "com.sybd.znld.onenet.realtime.";
    public static String getRedisRealtimeKey(String imei) {
        return REDIS_REALTIME_PREFIX + imei;
    }
    // 缓存中对应资源的key
    public static final String REDIS_MAP_KEY_BRIGHTNESS = "brightness"; // 实时亮度
    public static final String REDIS_MAP_KEY_ENERGY = "energy"; // 一小时内累计电量
    public static final String REDIS_MAP_KEY_TOTAL_ENERGY = "totalEnergy"; // 总的累计电量，从第一次通电开始
    public static final String REDIS_MAP_KEY_LAST_STATISTICS_UPDATE_TIME = "lastStatisticsUpdateTime"; // 统计数据最后更新时间
    public static final String REDIS_MAP_KET_EXECUTION_MODE = "executionMode"; // 当前路灯执行模式，手动或策略
    public static final String REDIS_MAP_KEY_ONENET_UP_MSG = "onenetUpMsg"; // 硬件上传数据
    public static final String REDIS_MAP_KEY_ONENET_UP_MSG_AT = "onenetUpMsgAt"; // 单灯数据的更新时间
    public static final String REDIS_MAP_KEY_IS_LIGHT = "isLight"; // 是否亮灯
    public static final String REDIS_MAP_KEY_IS_FAULT = "isFault"; // 是否故障
    public static final String REDIS_MAP_KEY_IS_ONLINE = "isOnline"; // 是否在线
    public static final String REDIS_MAP_KEY_BAIDU_LAT = "baiduLat"; // 百度纬度
    public static final String REDIS_MAP_KEY_BAIDU_LNG = "baiduLng"; // 百度经度
    public static final String REDIS_MAP_KEY_PM25 = "pm25";
    public static final String REDIS_MAP_KEY_PM10 = "pm10";
    public static final String REDIS_MAP_KEY_X_ANGLE = "xAngle";
    public static final String REDIS_MAP_KEY_Y_ANGLE = "yAngle";
    public static final String REDIS_MAP_KEY_O3 = "o3";
    public static final String REDIS_MAP_KEY_SO2 = "so2";
    public static final String REDIS_MAP_KEY_NO2 = "no2";
    public static final String REDIS_MAP_KEY_CO = "co";
    public static final String REDIS_MAP_KEY_TEMPERATURE = "temperature";
    public static final String REDIS_MAP_KEY_HUMIDITY = "humidity";
    public static final String REDIS_MAP_KEY_IS_PAD_ON = "isPadOn"; // 互动屏开关状态
    public static final String REDIS_MAP_KEY_IS_ALARM_ON = "isAlarmOn"; // 一键报警开关
    public static final String REDIS_MAP_KEY_IS_PROBE_ON = "isProbeOn"; // 探针开关
    public static final String REDIS_MAP_KEY_IS_MINISTAR_ON = "isMinistarOn"; // 景观灯开关
    public static final String REDIS_MAP_KEY_IS_SCREEN_ON = "isScreenOn"; // 电子屏开关
    public static final String REDIS_MAP_KEY_IS_FAN_ON = "isFanOn"; // 风扇开关
}
