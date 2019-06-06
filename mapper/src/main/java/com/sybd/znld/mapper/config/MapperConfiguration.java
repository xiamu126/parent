package com.sybd.znld.mapper.config;

import com.sybd.znld.mapper.db.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
public class MapperConfiguration {
    @Bean("oauthDataSource")
    @ConfigurationProperties("spring.datasource.oauth")
    @ConditionalOnMissingBean(name = {"oauthDataSource"})
    public DataSource oauthDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("znldDataSource")
    @ConfigurationProperties("spring.datasource.znld")
    @ConditionalOnMissingBean(name = {"znldDataSource"})
    public DataSource znldDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("rbacDataSource")
    @ConfigurationProperties("spring.datasource.rbac")
    @ConditionalOnMissingBean(name = {"rbacDataSource"})
    public DataSource rbacDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("ministarDataSource")
    @ConfigurationProperties("spring.datasource.ministar")
    @ConditionalOnMissingBean(name = {"ministarDataSource"})
    public DataSource ministarDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    @DependsOn({"oauthDataSource","znldDataSource","rbacDataSource", "ministarDataSource"})
    @ConditionalOnMissingBean
    public DynamicDataSource dataSource(@Qualifier("oauthDataSource") DataSource oauthDataSource,
                                        @Qualifier("znldDataSource") DataSource znldDataSource,
                                        @Qualifier("rbacDataSource") DataSource rbacDataSource,
                                        @Qualifier("ministarDataSource") DataSource ministarDataSource) {
        var targetDataSources = new HashMap<>();
        targetDataSources.put("oauth", oauthDataSource);
        targetDataSources.put("znld", znldDataSource);
        targetDataSources.put("rbac", rbacDataSource);
        targetDataSources.put("ministar", ministarDataSource);
        return new DynamicDataSource(znldDataSource, targetDataSources);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "oauthTransactionManager")
    public PlatformTransactionManager oauthTransactionManager(@Qualifier("oauthDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(name = "znldTransactionManager")
    public PlatformTransactionManager znldTransactionManager(@Qualifier("znldDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(name = "rbacTransactionManager")
    public PlatformTransactionManager rbacTransactionManager(@Qualifier("rbacDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(name = "ministarTransactionManager")
    public PlatformTransactionManager ministarTransactionManager(@Qualifier("ministarDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
