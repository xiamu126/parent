package com.sybd.znld.ministar.config;

import com.sybd.znld.model.BaseApiResult;
import com.sybd.znld.model.lamp.HttpLogModel;
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

@Slf4j
@Aspect
@Component
public class AopConfig {
    private final RedissonClient redissonClient;

    @Autowired
    public AopConfig(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
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
                var httpLog = new HttpLogModel();
                httpLog.path = request.getRequestURI();
                httpLog.method = request.getMethod();
                httpLog.header = "";
                httpLog.body = "";
                httpLog.ip = request.getRemoteAddr();
                requestId = request.getHeader("requestId");
                //this.logService.addLog(httpLog);
                //log.debug(request.getRemoteHost());
                //log.debug(String.valueOf(request.getRemotePort()));
            }
        }
        try {
            /*if(MyString.isEmptyOrNull(requestId)){
                return BaseApiResult.fail("参数错误");
            }
            var count = this.redissonClient.getKeys().countExists(requestId);
            if(count > 0){ // key不存在
                return BaseApiResult.fail("重复请求");
            }
            this.redissonClient.getBucket(requestId).set(""); // 保存请求id，防止重复请求
            this.redissonClient.getKeys().expire(requestId, 5, TimeUnit.MINUTES);*/
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(throwable.getMessage());
        }
        return BaseApiResult.fail("发生错误");
    }
}
