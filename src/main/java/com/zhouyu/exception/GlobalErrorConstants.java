package com.zhouyu.exception;

/**
 *
 * @author shanweidi
 * @since 2026-05-08 15:54
 **/
public interface GlobalErrorConstants {
    ErrorCode SUCCESS = new ErrorCode(0,"成功");

    ErrorCode BAD_REQUEST = new ErrorCode(40001,"请求参数错误");
    ErrorCode BAD_POST = new ErrorCode(40002,"岗位有误，查无此岗位");
    ErrorCode BAD_RELATION = new ErrorCode(40003,"参数有误，该部门下无此岗位");
    ErrorCode INTERNAL_ERROR = new ErrorCode(50001,"服务内部异常");
}
