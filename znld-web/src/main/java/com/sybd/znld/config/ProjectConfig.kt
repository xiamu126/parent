package com.sybd.znld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "project")
class ProjectConfig {
    lateinit var name: String;
    lateinit var cachePrefix: String ;
    lateinit var cacheOfNullExpirationTime: Duration;
    lateinit var cacheOfCaptchaExpirationTime: Duration;
    lateinit var auth2TokenExpirationTime: Duration;
}
