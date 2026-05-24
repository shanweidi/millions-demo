package com.zhouyu.exception;

import com.zhouyu.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author shanweidi
 * @since 2026-05-08 16:20
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public Result<?> handleException(Exception ex) {
        logger.error("[globalExceptionHandler]",ex);
        if (ex instanceof IllegalArgumentException) {
            return Result.error(GlobalErrorConstants.BAD_REQUEST);
        }
        return Result.error(GlobalErrorConstants.INTERNAL_ERROR);
    }
}
