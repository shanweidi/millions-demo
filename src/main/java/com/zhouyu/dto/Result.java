package com.zhouyu.dto;

import com.zhouyu.exception.ErrorCode;
import com.zhouyu.exception.GlobalErrorConstants;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author shanweidi
 * @since 2026-05-08 15:45
 **/
@Data
public class Result<T> implements Serializable {

    private Integer code;

    private String message;

    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = GlobalErrorConstants.SUCCESS.getCode();
        result.data = data;
        result.message = "ok";
        return result;
    }

    public static <T> Result<T> error(Integer code,String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }
}
