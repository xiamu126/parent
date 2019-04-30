package com.sybd.znld.web.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
//设置AOP执行顺序(需要在事务之前，否则事务只发生在默认库中)
@Order(1)
@Component
public class DataSourceAspect {
    //@Pointcut("@annotation(com.sybd.znld.mapper.db.DbSource)|| within(com.sybd.znld.video.service..*)") //within针对class级别的注解
    @Pointcut("@within(com.sybd.znld.web.db.DbSource)") //within针对class级别的注解
    public void dataSourcePointCut() {}

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 优先处理方法上的注解
        var signature = (MethodSignature) point.getSignature();
        var method = signature.getMethod();
        var ds = method.getAnnotation(DbSource.class);
        if(ds != null){// 方法上的注解
            // 通过判断DataSource中的值来判断当前方法应用哪个数据源
            DynamicDataSourceConfig.DynamicDataSource.setDataSource(ds.value());
            try {
                return point.proceed();
            } finally {
                DynamicDataSourceConfig.DynamicDataSource.clearDataSource();
            }
        }
        // 其次处理Mapper上的注解
        var interfaces = point.getTarget().getClass().getInterfaces(); // 如果是代理类，即其所实现了的所有接口
        for(var i: interfaces){// 如果是Mapper
            ds = i.getAnnotation(DbSource.class);
            if(ds != null){
                DynamicDataSourceConfig.DynamicDataSource.setDataSource(ds.value());
                try {
                    return point.proceed();
                } finally {
                    DynamicDataSourceConfig.DynamicDataSource.clearDataSource();
                }
            }
        }
        // 最后才是类上的注解
        ds = point.getTarget().getClass().getAnnotation(DbSource.class);
        if(ds != null){// 类上的注解（包括接口）
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
