package com.sybd.znld.web.config;

public class RedisKeyConfig {
    public static final String CLIENT_CHANNEL_GUID_GLOBAL_PREFIX = "znld:web:client:channelGuid:global:";
    public static final String CLIENT_CHANNEL_GUID_GLOBAL_PREFIX_MATCH = "znld:web:client:channelGuid:global:*";
    public static final String CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX = "znld:web:client:channelGuid:persistence:";
    public static final String CLIENT_CHANNEL_GUID_PREFIX = "znld:web:client:channelGuid:";
    public static final String CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX_MATCH = "znld:web:client:channelGuid:persistence:*";
    public static final String TASK_ONENET_HEART_BEAT = "znld:web:task:onenet:heartbeat";
    public static final String RLOCK_CHANNEL_GLOBAL_REGISTER = "znld:web:rlock:channel:global:register";
}
