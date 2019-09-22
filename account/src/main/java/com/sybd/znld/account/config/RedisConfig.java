package com.sybd.znld.account.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

@EnableCaching(proxyTargetClass = true)
@Configuration
public class RedisConfig {
    @Value("${spring.profiles.active}")
    private String environment;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        var config = Config.fromYAML(new ClassPathResource("redisson-single-prod.yml").getInputStream());
        if(this.environment.equalsIgnoreCase("dev")){
            config = Config.fromYAML(new ClassPathResource("redisson-single-dev.yml").getInputStream());
        }
        return Redisson.create(config);
    }
}
