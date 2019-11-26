package com.sybd.znld.mapper.config;

import com.sybd.znld.mapper.EnumTypeHandler;
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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.sybd.znld.mapper.oauth", sqlSessionFactoryRef="oauthSqlSessionFactory")
public class OAuthDataSourceConfig {
    @Bean("oauthDataSource")
    @ConfigurationProperties("spring.datasource.oauth")
    @ConditionalOnMissingBean(name = {"oauthDataSource"})
    public DataSource oauthDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("oauthTransactionManager")
    @Primary
    @ConditionalOnMissingBean(name = "oauthTransactionManager")
    public PlatformTransactionManager oauthTransactionManager(@Qualifier("oauthDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "oauthSqlSessionFactory")
    @Primary
    @ConditionalOnMissingBean(name = "oauthSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("oauthDataSource") DataSource dataSource) throws Exception {
        var factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/oauth/*.xml"));
        var bean = factory.getObject();
        assert bean != null;
        bean.getConfiguration().setMapUnderscoreToCamelCase(true);
        var typeHandlerRegistry = bean.getConfiguration().getTypeHandlerRegistry();
        //typeHandlerRegistry.register(Status.class, EnumTypeHandler.class);
        typeHandlerRegistry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        return bean;
    }
}
