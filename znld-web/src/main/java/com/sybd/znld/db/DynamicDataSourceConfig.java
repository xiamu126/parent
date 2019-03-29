package com.sybd.znld.db;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Configuration
public class DynamicDataSourceConfig {
    @Bean("oauthDataSource")
    @ConfigurationProperties("spring.datasource.druid.oauth")
    public DataSource oauthDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("znldDataSource")
    @ConfigurationProperties("spring.datasource.druid.znld")
    public DataSource znldDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("rbacDataSource")
    @ConfigurationProperties("spring.datasource.druid.rbac")
    public DataSource rbacDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("ministarDataSource")
    @ConfigurationProperties("spring.datasource.druid.ministar")
    public DataSource ministarDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    @DependsOn({"oauthDataSource","znldDataSource","rbacDataSource", "ministarDataSource"})
    public DynamicDataSource dataSource(@Qualifier("oauthDataSource") DataSource oauthDataSource,
                                        @Qualifier("znldDataSource") DataSource znldDataSource,
                                        @Qualifier("rbacDataSource") DataSource rbacDataSource,
                                        @Qualifier("ministarDataSource") DataSource ministarDataSource) {
        var targetDataSources = new HashMap<Object, Object>();
        targetDataSources.put("oauth", oauthDataSource);
        targetDataSources.put("znld", znldDataSource);
        targetDataSources.put("rbac", rbacDataSource);
        targetDataSources.put("ministar", ministarDataSource);
        return new DynamicDataSource(znldDataSource, targetDataSources);
    }

    public static class DynamicDataSource extends AbstractRoutingDataSource {
        private static final ThreadLocal<Stack<String>> contextHolder = new ThreadLocal<>();
        static {
            contextHolder.set(new Stack<>());
        }
        /**
         * 配置DataSource, defaultTargetDataSource为主数据库
         */
        public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
            super.setDefaultTargetDataSource(defaultTargetDataSource);
            super.setTargetDataSources(targetDataSources);
            super.afterPropertiesSet();
        }

        @Override
        protected Object determineCurrentLookupKey() {
            return getDataSource();
        }

        static void setDataSource(String dataSource) {
            contextHolder.get().push(dataSource);
        }

        private static String getDataSource() {
            return contextHolder.get().peek();
        }

        static void clearDataSource() {
            contextHolder.get().pop();
        }

    }
}
