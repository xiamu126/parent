package com.sybd.znld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "project")
public class ProjectConfig {
    public String name;
    public String cachePrefix;
    public Duration cacheOfNullExpirationTime;
    public Duration cacheOfCaptchaExpirationTime;
    public Duration auth2TokenExpirationTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public Duration getCacheOfNullExpirationTime() {
        return cacheOfNullExpirationTime;
    }

    public void setCacheOfNullExpirationTime(Duration cacheOfNullExpirationTime) {
        this.cacheOfNullExpirationTime = cacheOfNullExpirationTime;
    }

    public Duration getCacheOfCaptchaExpirationTime() {
        return cacheOfCaptchaExpirationTime;
    }

    public void setCacheOfCaptchaExpirationTime(Duration cacheOfCaptchaExpirationTime) {
        this.cacheOfCaptchaExpirationTime = cacheOfCaptchaExpirationTime;
    }

    public Duration getAuth2TokenExpirationTime() {
        return auth2TokenExpirationTime;
    }

    public void setAuth2TokenExpirationTime(Duration auth2TokenExpirationTime) {
        this.auth2TokenExpirationTime = auth2TokenExpirationTime;
    }
}
