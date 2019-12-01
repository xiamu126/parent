package com.sybd.znld.light.config;

import com.sybd.znld.light.controller.dto.BaseStrategy;
import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;

@Slf4j
@Aspect
@Component
public class AopConfig {
    private final RedissonClient redissonClient;
    private final ProjectConfig projectConfig;

    @Autowired
    public AopConfig(RedissonClient redissonClient, ProjectConfig projectConfig) {
        this.redissonClient = redissonClient;
        this.projectConfig = projectConfig;
    }

    @Pointcut("execution(public * com.sybd.znld.*.controller..*.*(..))") //com.sybd.znld.web.controller..切入任何定义在controller或其子包下的
    public void checkControllerParam(){}

    @Around("checkControllerParam()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        return executeAopAround(proceedingJoinPoint);
    }

    private Object executeAopAround(ProceedingJoinPoint proceedingJoinPoint) {
        var args = proceedingJoinPoint.getArgs();
        String requestId = null;
        for(var arg : args){
            if(arg instanceof BindingResult){
                var bindingResult = (BindingResult) arg;
                if (bindingResult.hasErrors()) return BaseApiResult.fail("非法的参数");
            }
            if(arg instanceof HttpServletRequest){
                var request = (HttpServletRequest)arg;
            }
            if(BaseStrategy.class.isAssignableFrom(arg.getClass())){
                if(!MyString.isEmptyOrNull(this.projectConfig.zoneId) &&
                        ZoneId.getAvailableZoneIds().contains(this.projectConfig.zoneId)){
                    var s = (BaseStrategy) arg;
                    s.zoneId = this.projectConfig.zoneId;
                }
            }
        }
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(throwable.getMessage());
        }
        return null;
    }
}
