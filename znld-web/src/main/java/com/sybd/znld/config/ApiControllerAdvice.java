package com.sybd.znld.config;

import com.sybd.znld.core.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice //只处理controller层的异常
public class ApiControllerAdvice {
    private final Logger log = LoggerFactory.getLogger(ApiControllerAdvice.class);

    @ExceptionHandler(value = Exception.class)
    public ApiResult exceptionHandler(Exception e) {
        log.error("全局API未处理异常：" + e.getMessage());
        ApiResult result = new ApiResult();
        result.setCode(1);
        if(e instanceof NoHandlerFoundException){
            result.setMsg("404");
        }else if(e instanceof AccessDeniedException){
            result.setMsg("403");
        } else{
            result.setMsg("500");
        }
        return result;
    }
}
