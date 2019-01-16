package com.sybd.znld.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

public class RedisKeyConfig {
    public static final String CLIENT_CHANNEL_GUID_GLOBAL_PREFIX = "znld:web:client:channelGuid:global:";
    public static final String CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX = "znld:web:client:channelGuid:persistence:";
    public static final String CLIENT_CHANNEL_GUID_PREFIX = "znld:web:client:channelGuid:";
    public static final String CLIENT_CHANNEL_GUID_PERSISTENCE_PREFIX_MATCH = "znld:web:client:channelGuid:persistence:*";
    public static final String TASK_ONENET_HEART_BEAT = "znld:web:task:onenet:heartbeat";
}
