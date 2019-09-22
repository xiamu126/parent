package com.sybd.znld.account.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties(prefix = "project")
@Getter
@Setter
@Component
public class ProjectConfig {
    public Duration auth2TokenExpirationTime;
}
