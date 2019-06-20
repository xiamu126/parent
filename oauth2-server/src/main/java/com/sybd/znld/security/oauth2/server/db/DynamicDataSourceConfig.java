package com.sybd.znld.security.oauth2.server.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfig {
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

    private static final String defaultDataSource = "oauth";

    @Bean
    @Primary
    @DependsOn({"oauthDataSource","znldDataSource","rbacDataSource", "ministarDataSource"})
    @ConditionalOnMissingBean
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

    @Bean("oauthTransactionManager")
    @Primary
    @ConditionalOnMissingBean(name = "oauthTransactionManager")
    public PlatformTransactionManager oauthTransactionManager(@Qualifier("oauthDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("znldTransactionManager")
    @ConditionalOnMissingBean(name = "znldTransactionManager")
    public PlatformTransactionManager znldTransactionManager(@Qualifier("znldDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("rbacTransactionManager")
    @ConditionalOnMissingBean(name = "rbacTransactionManager")
    public PlatformTransactionManager rbacTransactionManager(@Qualifier("rbacDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("ministarTransactionManager")
    @ConditionalOnMissingBean(name = "ministarTransactionManager")
    public PlatformTransactionManager ministarTransactionManager(@Qualifier("ministarDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    public static class DynamicDataSource extends AbstractRoutingDataSource {
        private static final ThreadLocal<ArrayDeque<String>> threadLocal = new ThreadLocal<>();
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
            var stack = threadLocal.get();
            if(stack == null){
                stack = new ArrayDeque<>();
                threadLocal.set(stack);
            }
            stack.push(dataSource);
        }

        private static String getDataSource() {
            var stack = threadLocal.get();
            if(stack == null) return null;
            return stack.peek();
        }

        public static void clearDataSource() {
            var stack = threadLocal.get();
            if(stack != null){
                if(!stack.isEmpty()) stack.pop();
                if(stack.isEmpty()) threadLocal.remove();
            }
        }
    }
}
