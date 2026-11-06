package com.cloud.common.exception;

import com.cloud.common.enums.ResultCode;

/**
 * 全局业务异常，统一交由 {@link GlobalExceptionHandler} 处理。
 */
public class GlobalException extends RuntimeException {


    private final int code;

    public GlobalException(String message) {
        this(ResultCode.BUSINESS_ERROR.getCode(), message);
    }

    public GlobalException(int code, String message) {
        //super是调父类的方法
        super(message);
        this.code = code;
    }

    public GlobalException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public GlobalException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
