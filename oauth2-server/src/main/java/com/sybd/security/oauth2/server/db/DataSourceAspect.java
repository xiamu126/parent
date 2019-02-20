package com.sybd.security.oauth2.server.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
//设置AOP执行顺序(需要在事务之前，否则事务只发生在默认库中)
//在主从分离的情况，这个没什么必要，因为一旦开启事务了必然要在主数据库上发生
//当然，如果多个数据源并没有什么关系的话，这个就需要了
//就像现在的情况，一个访问znld数据库，一个访问oauth2数据库
@Order(1)
@Component
public class DataSourceAspect {

    private final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut("@annotation(com.sybd.security.oauth2.server.db.DbSource)")
    public void dataSourcePointCut() {}

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DbSource ds = method.getAnnotation(DbSource.class);
        // 通过判断DataSource中的值来判断当前方法应用哪个数据源
        DynamicDataSourceConfig.DynamicDataSource.setDataSource(ds.value());
        log.debug("set datasource is " + ds.value());
        try {
            return point.proceed();
        } finally {
            DynamicDataSourceConfig.DynamicDataSource.clearDataSource();
            log.debug("clear datasource");
        }
    }
}
