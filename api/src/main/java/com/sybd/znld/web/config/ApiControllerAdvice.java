package com.sybd.znld.web.config;

import com.sybd.znld.model.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice //只处理controller层的异常
public class ApiControllerAdvice {
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
