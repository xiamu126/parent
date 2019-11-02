package com.sybd.znld.environment.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@EnableCaching(proxyTargetClass = true)
@Configuration
public class RedisConfig {
    @Value("${spring.profiles.active}")
    private String environment;

    @Primary
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        var config = Config.fromYAML(new ClassPathResource("redisson-single-prod.yml").getInputStream());
        if(this.environment.equalsIgnoreCase("dev")){
            config = Config.fromYAML(new ClassPathResource("redisson-single-dev.yml").getInputStream());
        }
        return Redisson.create(config);
    }

    @Bean(name = "account-redis", destroyMethod = "shutdown")
    public RedissonClient AccountRedssion() throws IOException {
        var config = Config.fromYAML(new ClassPathResource("redisson-single-account-prod.yml").getInputStream());
        if(this.environment.equalsIgnoreCase("dev")){
            config = Config.fromYAML(new ClassPathResource("redisson-single-account-dev.yml").getInputStream());
        }
        return Redisson.create(config);
    }
}
