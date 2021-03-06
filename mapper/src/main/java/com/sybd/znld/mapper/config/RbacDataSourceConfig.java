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
@MapperScan(basePackages = "com.sybd.znld.mapper.rbac", sqlSessionFactoryRef = "rbacSqlSessionFactory")
public class RbacDataSourceConfig {
    @Bean("rbacDataSource")
    @ConfigurationProperties("spring.datasource.rbac")
    @ConditionalOnMissingBean(name = {"rbacDataSource"})
    public DataSource rbacDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("rbacTransactionManager")
    @ConditionalOnMissingBean(name = "rbacTransactionManager")
    public PlatformTransactionManager rbacTransactionManager(@Qualifier("rbacDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "rbacSqlSessionFactory")
    @ConditionalOnMissingBean(name = "rbacSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("rbacDataSource") DataSource dataSource) throws Exception {
        var factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/rbac/*.xml"));
        var bean = factory.getObject();
        assert bean != null;
        bean.getConfiguration().setMapUnderscoreToCamelCase(true);
        var typeHandlerRegistry = bean.getConfiguration().getTypeHandlerRegistry();
        typeHandlerRegistry.register(Status.class, MyEnumTypeHandler.class);
        typeHandlerRegistry.setDefaultEnumTypeHandler(MyEnumTypeHandler.class);
        return bean;
    }
}
