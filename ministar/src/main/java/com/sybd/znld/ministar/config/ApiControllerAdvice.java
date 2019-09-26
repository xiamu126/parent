package com.sybd.znld.ministar.config;

import com.sybd.znld.model.ApiResult;
import com.sybd.znld.model.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice //只处理controller层的异常
public class ApiControllerAdvice {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public BaseApiResult methodArgumentNotValidExceptionHandler(Exception ex, HttpServletRequest request){
        log.error(ex.getMessage());
        ex.printStackTrace();
        var result = new BaseApiResult();
        result.code = 1;
        result.msg = "参数错误";
        return result;
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExceptionHandler(value = Exception.class)
    public ApiResult exceptionHandler(Exception ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        ApiResult result = new ApiResult();
        result.setCode(1);
        result.msg = "发生错误";
        if(ex instanceof NoHandlerFoundException){
            result.setMsg("404");
        }else if(ex instanceof AccessDeniedException){
            result.setMsg("403");
        } else{
            result.setMsg("500");
        }
        return result;
    }
}
