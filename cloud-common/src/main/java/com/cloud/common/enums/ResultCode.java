package com.cloud.common.enums;

/**
 * 全局响应码。
 */
public enum ResultCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "非法请求"),
    UNAUTHORIZED(401, "未登录或凭证无效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    SERVER_ERROR(500, "服务器内部错误"),
    BUSINESS_ERROR(1001, "业务异常"),
    REMOTE_CALL_ERROR(1002, "远程调用失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
