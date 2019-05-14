package com.sybd.znld.web.config;

import com.sybd.znld.util.MyString;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;

@EnableRedisHttpSession
@EnableCaching(proxyTargetClass = true)
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.profiles.active}")
    private String environment;

    @Bean
    public StringRedisTemplate stringRedisTemplate(@Qualifier("MyRedisConnectionFactory") RedisConnectionFactory factory){

        /* SpringBoot扩展了ClassLoader，进行分离打包的时候，使用到JdkSerializationRedisSerializer的地方
         * 会因为ClassLoader的不同导致加载不到Class
         * 指定使用项目的ClassLoader
         * JdkSerializationRedisSerializer默认使用{@link sun.misc.Launcher.AppClassLoader}
         * SpringBoot默认使用{@link org.springframework.boot.loader.LaunchedURLClassLoader}*/

        return new StringRedisTemplate(factory);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("MyRedisConnectionFactory") RedisConnectionFactory factory){
        var redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean(name = "MyRedisConnectionFactory")
    public RedisConnectionFactory lettuceConnectionFactory(GenericObjectPoolConfig config) {
        // 单机版配置
        var redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        if(!MyString.isEmptyOrNull(password)){
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }
        // 集群版配置
        /*var clusterNodes = "";
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        String[] serverArray = clusterNodes.split(","); //"127.0.0.1:30001", "127.0.0.1:30002", "127.0.0.1:30003"
        var nodes = new HashSet<RedisNode>();
        for (var ipPort : serverArray) {
            var ipAndPort = ipPort.split(":");
            nodes.addUser(new RedisNode(ipAndPort[0].trim(), Integer.valueOf(ipAndPort[1])));
        }
        redisClusterConfiguration.setPassword(RedisPassword.of(password));
        redisClusterConfiguration.setClusterNodes(nodes);*/

        // 客户端配置
        var lettuceClientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
        //return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        var config = Config.fromYAML(new ClassPathResource("redisson-single-prod.yml").getInputStream());
        if(this.environment.equalsIgnoreCase("dev")){
            config = Config.fromYAML(new ClassPathResource("redisson-single-dev.yml").getInputStream());
        }
        return Redisson.create(config);
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        var factory = lettuceConnectionFactory(genericObjectPoolConfig());
        var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();//.prefixKeysWith("sybd::znld::web");//.entryTtl(Duration.ofSeconds(30));
        return RedisCacheManager.builder(factory).cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }

    public static class CustomKeyGenerator implements KeyGenerator {
        public Object generate(Object target, Method method, Object... params) {
            return target.getClass().getName() + "." + method.getName() + "::"
                    + (params.length <= 0 ? "null" : StringUtils.arrayToDelimitedString(params, "_"));
        }
    }
}
