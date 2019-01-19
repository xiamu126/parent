package com.sybd.security.oauth2.server.db;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect @Order(1) //设置AOP执行顺序(需要在事务之前，否则事务只发生在默认库中)
@Component
public class DataSourceAspect {
    @Pointcut("@annotation(com.sybd.security.oauth2.server.db.DbSource)")
    public void dataSourcePointCut() {}

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        var signature = (MethodSignature) point.getSignature();
        var method = signature.getMethod();
        var ds = method.getAnnotation(DbSource.class);
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
