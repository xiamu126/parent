package com.sybd.znld.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

//@Component
@ConfigurationProperties(prefix = "project")
@Getter @Setter
public class ProjectConfig {
    public String name;
    public String cachePrefix;
    public Duration cacheOfNullExpirationTime;
    public Duration cacheOfCaptchaExpirationTime;
    public Duration auth2TokenExpirationTime;
}
