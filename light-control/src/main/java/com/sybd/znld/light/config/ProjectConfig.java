package com.sybd.znld.light.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {
    @Value("${zone-id}")
    public String zoneId;
}
