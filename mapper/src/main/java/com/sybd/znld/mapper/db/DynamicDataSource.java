package com.sybd.znld.mapper.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.ArrayDeque;
import java.util.Map;

@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
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
        threadLocal.set(dataSource);
    }

    private static String getDataSource() {
        return threadLocal.get();
    }

    public static void clearDataSource() {
        threadLocal.remove();
    }
}