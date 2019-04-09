package com.sybd.security.oauth2.server.db;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

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

    private static final String defaultDataSource = "oauth";

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
        return new DynamicDataSource(oauthDataSource, targetDataSources);
    }

    public static class DynamicDataSource extends AbstractRoutingDataSource {
        private static final ThreadLocal<ArrayDeque<String>> contextHolder = new ThreadLocal<>();
        static {
            var stack = new ArrayDeque<String>();
            stack.add(defaultDataSource);
            contextHolder.set(stack);
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

        public static void setDataSource(String dataSource) {
            var stack = contextHolder.get();
            if(stack == null){
                stack = new ArrayDeque<>();
                contextHolder.set(stack);
            }
            stack.push(dataSource);
        }

        private static String getDataSource() {
            var stack = contextHolder.get();
            if(stack == null){
                stack = new ArrayDeque<>();
                contextHolder.set(stack);
                return defaultDataSource;
            }
            var ret = stack.peek();
            if(ret == null) return defaultDataSource;
            return ret;
        }

        public static void clearDataSource() {
            contextHolder.get().pop();
        }
    }
}
