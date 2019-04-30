package com.sybd.znld.web.config;

import com.sybd.znld.web.service.znld.ILogService;
import com.sybd.znld.znld.core.ApiResult;
import com.sybd.znld.model.lamp.HttpLogModel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AopConfig {

    private final Logger log = LoggerFactory.getLogger(AopConfig.class);
    private final ILogService logService;

    @Autowired
    public AopConfig(ILogService logService) {
        this.logService = logService;
    }

    @Pointcut("execution(public * com.sybd.znld.web.controller..*.*(..))") //com.sybd.znld.web.controller..切入任何定义在controller或其子包下的
    public void checkControllerParam(){}

    @Around("checkControllerParam()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        return executeAopAround(proceedingJoinPoint);
    }

    private Object executeAopAround(ProceedingJoinPoint proceedingJoinPoint) {
        var args = proceedingJoinPoint.getArgs();
        for(var arg : args){
            if(arg instanceof BindingResult){
                var bindingResult = (BindingResult)arg;
                if (bindingResult.hasErrors()) return ApiResult.fail("非法的参数");
            }
            if(arg instanceof HttpServletRequest){
                var request = (HttpServletRequest)arg;
                var httpLog = new HttpLogModel();
                httpLog.path = request.getRequestURI();
                httpLog.method = request.getMethod();
                httpLog.header = "";
                httpLog.body = "";
                httpLog.ip = request.getRemoteAddr();
                //this.logService.addLog(httpLog);
                //log.debug(request.getRemoteHost());
                //log.debug(String.valueOf(request.getRemotePort()));
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
