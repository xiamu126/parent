package com.sybd.znld.mapper.config;

import com.sybd.znld.mapper.MyEnumTypeHandler;
import com.sybd.znld.model.Status;
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
@MapperScan(basePackages = "com.sybd.znld.mapper.ministar", sqlSessionFactoryRef="ministarSqlSessionFactory")
public class MinistarDataSourceConfig {
    @Bean("ministarDataSource")
    @ConfigurationProperties("spring.datasource.ministar")
    @ConditionalOnMissingBean(name = {"ministarDataSource"})
    public DataSource ministarDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("ministarTransactionManager")
    @ConditionalOnMissingBean(name = "ministarTransactionManager")
    public PlatformTransactionManager ministarTransactionManager(@Qualifier("ministarDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "ministarSqlSessionFactory")
    @ConditionalOnMissingBean(name = {"ministarSqlSessionFactory"})
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("ministarDataSource") DataSource dataSource) throws Exception {
        var factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/ministar/*.xml"));
        var bean = factory.getObject();
        assert bean != null;
        bean.getConfiguration().setMapUnderscoreToCamelCase(true);
        var typeHandlerRegistry = bean.getConfiguration().getTypeHandlerRegistry();
        typeHandlerRegistry.register(Status.class, MyEnumTypeHandler.class);
        typeHandlerRegistry.setDefaultEnumTypeHandler(MyEnumTypeHandler.class);
        return bean;
    }
}
