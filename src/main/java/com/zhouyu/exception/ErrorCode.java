package com.zhouyu.exception;

import lombok.Data;

/**
 *
 * @author shanweidi
 * @since 2026-05-08 15:52
 **/
@Data
public class ErrorCode {

    private final Integer code;
    private final String message;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
