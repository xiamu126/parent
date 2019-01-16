package com.sybd.znld.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectConfig {
    private String name;
    private String cachePrefix;
    private Duration cacheOfNullExpirationTime;
    private Duration cacheOfCaptchaExpirationTime;
    private Duration auth2TokenExpirationTime;
}
