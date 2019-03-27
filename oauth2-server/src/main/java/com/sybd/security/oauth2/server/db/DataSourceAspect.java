package com.sybd.security.oauth2.server.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
//设置AOP执行顺序(需要在事务之前，否则事务只发生在默认库中)
@Order(1)
@Component
public class DataSourceAspect {
    private final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut("@annotation(com.sybd.security.oauth2.server.db.DbSource) || within(com.sybd.security.oauth2.server.service..*)") //within针对class级别的注解
    public void dataSourcePointCut() {}

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        var signature = (MethodSignature) point.getSignature();
        var method = signature.getMethod();
        var ds = method.getAnnotation(DbSource.class);
        if(ds != null){
            // 通过判断DataSource中的值来判断当前方法应用哪个数据源
            DynamicDataSourceConfig.DynamicDataSource.setDataSource(ds.value());
            try {
                return point.proceed();
            } finally {
                DynamicDataSourceConfig.DynamicDataSource.clearDataSource();
            }
        }
        ds = point.getTarget().getClass().getAnnotation(DbSource.class);
        if(ds != null){
            DynamicDataSourceConfig.DynamicDataSource.setDataSource(ds.value());
            try {
                return point.proceed();
            } finally {
                DynamicDataSourceConfig.DynamicDataSource.clearDataSource();
            }
        }
        return point.proceed();
    }
}
