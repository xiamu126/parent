package com.sybd.znld.mapper.db;

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
    @Pointcut("@within(com.sybd.znld.mapper.db.DbSource)") //within针对class级别的注解
    public void dataSourcePointCut() {}

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 处理Mapper上的注解
        var interfaces = point.getTarget().getClass().getInterfaces(); // 如果是代理类，即其所实现了的所有接口
        for(var i: interfaces){// 如果是Mapper
            var ds = i.getAnnotation(DbSource.class);
            if(ds != null){
                DynamicDataSource.setDataSource(ds.value());
                try {
                    return point.proceed();
                } finally {
                    DynamicDataSource.clearDataSource();
                }
            }
        }
        return point.proceed();
    }
}
