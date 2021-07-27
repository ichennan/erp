package com.fiveamazon.erp.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class SimpleExceptionHandler {
    @ResponseBody
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> dataAccessExceptionHandler(Throwable throwable){
        log.error("dataAccessExceptionHandler");
        log.error(throwable.getMessage(), throwable);
        Map<String, Object> result = new HashMap<>();
        result.put("errorCode", "-404");
        result.put("errorType", "数据库读写异常");
        result.put("errorMessage",
                throwable.getCause() == null ? throwable.getMessage() :
                        throwable.getCause().getCause() == null ? throwable.getCause().getMessage() :
                                throwable.getCause().getCause().getMessage());
        return result;
    }

    @ResponseBody
    @ExceptionHandler(SimpleCommonException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> simpleCommonExceptionHandler(SimpleCommonException e){
        log.error("simpleCommonExceptionHandler");
        log.error(e.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("errorCode", "-403");
        result.put("errorType", "异常");
        result.put("errorMessage", e.getMessage());
        return result;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> exceptionHandler(Throwable throwable){
        log.error("exceptionHandler");
        log.error(throwable.getMessage(), throwable);
        Map<String, Object> result = new HashMap<>();
        result.put("errorCode", "-405");
        result.put("errorType", "未知异常");
        result.put("errorMessage", throwable.getMessage());
        return result;
    }
}
