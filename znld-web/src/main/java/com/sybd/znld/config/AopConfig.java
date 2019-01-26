package com.sybd.znld.config;

import com.sybd.znld.core.ApiResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class AopConfig {

    private final Logger log = LoggerFactory.getLogger(AopConfig.class);

    @Pointcut("execution(public * com.sybd.znld.controller..*.*(..))") //com.sybd.znld.controller..切入任何定义在controller或其子包下的
    public void checkControllerParam(){}

    @Around("checkControllerParam()")
    public Object arround(ProceedingJoinPoint proceedingJoinPoint){
        return executeAopArround(proceedingJoinPoint);
    }

    private Object executeAopArround(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        for(Object arg : args){
            if(arg instanceof BindingResult){
                BindingResult bindingResult = (BindingResult)arg;
                if (bindingResult.hasErrors()) {
                    return ApiResult.fail("非法的参数");
                }
            }
        }
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage());
        }
        return null;
    }
}
