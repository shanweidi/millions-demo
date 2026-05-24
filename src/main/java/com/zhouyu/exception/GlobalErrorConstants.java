package com.zhouyu.exception;

/**
 *
 * @author shanweidi
 * @since 2026-05-08 15:54
 **/
public interface GlobalErrorConstants {
    ErrorCode SUCCESS = new ErrorCode(0,"成功");

    ErrorCode BAD_REQUEST = new ErrorCode(40001,"请求参数错误");
    ErrorCode INTERNAL_ERROR = new ErrorCode(50001,"服务内部异常");
}
