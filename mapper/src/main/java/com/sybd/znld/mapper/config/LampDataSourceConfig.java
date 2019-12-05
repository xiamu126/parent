package com.sybd.znld.mapper.config;

import com.sybd.znld.mapper.MyEnumTypeHandler;
import com.sybd.znld.model.DeviceStatus;
import com.sybd.znld.model.Status;
import com.sybd.znld.model.StrategyStatus;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.sybd.znld.mapper.lamp", sqlSessionFactoryRef="znldSqlSessionFactory")
public class LampDataSourceConfig {
    @Bean("znldDataSource")
    @ConfigurationProperties("spring.datasource.znld")
    @ConditionalOnMissingBean(name = {"znldDataSource"})
    public DataSource znldDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("znldTransactionManager")
    @ConditionalOnMissingBean(name = "znldTransactionManager")
    public PlatformTransactionManager znldTransactionManager(@Qualifier("znldDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "znldSqlSessionFactory")
    @ConditionalOnMissingBean(name = {"znldSqlSessionFactory"})
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("znldDataSource") DataSource dataSource) throws Exception {
        var factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/lamp/*.xml"));
        var bean = factory.getObject();
        assert bean != null;
        bean.getConfiguration().setMapUnderscoreToCamelCase(true);
        var typeHandlerRegistry = bean.getConfiguration().getTypeHandlerRegistry();
        typeHandlerRegistry.register(Status.class, MyEnumTypeHandler.class);
        typeHandlerRegistry.register(DeviceStatus.class, MyEnumTypeHandler.class);
        typeHandlerRegistry.register(StrategyStatus.class, MyEnumTypeHandler.class);
        typeHandlerRegistry.setDefaultEnumTypeHandler(MyEnumTypeHandler.class);
        return bean;
    }
}
