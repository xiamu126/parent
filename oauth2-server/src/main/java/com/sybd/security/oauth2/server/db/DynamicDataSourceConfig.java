package com.sybd.security.oauth2.server.db;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.var;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfig {
    @Bean("oauthDataSource")
    @ConfigurationProperties("spring.datasource.druid.oauth")
    public DataSource dataSource1(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("znldDataSource")
    @ConfigurationProperties("spring.datasource.druid.znld")
    public DataSource dataSource2(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(@Qualifier("oauthDataSource") DataSource oauthDataSource, @Qualifier("znldDataSource") DataSource znldDataSource) {
        var targetDataSources = new HashMap<Object, Object>(2);
        targetDataSources.put("oauth", oauthDataSource);
        targetDataSources.put("znld", znldDataSource);
        return new DynamicDataSource(oauthDataSource, targetDataSources);
    }

    public static class DynamicDataSource extends AbstractRoutingDataSource {
        private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
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
            contextHolder.set(dataSource);
        }

        private static String getDataSource() {
            return contextHolder.get();
        }

        static void clearDataSource() {
            contextHolder.remove();
        }

    }
}
