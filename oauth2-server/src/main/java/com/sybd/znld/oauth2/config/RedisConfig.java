package com.sybd.znld.oauth2.config;

import com.sybd.znld.util.MyString;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.io.IOException;

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
