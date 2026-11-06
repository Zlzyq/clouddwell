package com.cloud.common.context;

/**
 * ThreadLocal 用户上下文：仅存当前请求的登录用户 ID。
 */
public final class UserContext {

    private static final ThreadLocal<Long> UID = new ThreadLocal<>();

    public static void setUid(Long uid) {
        UID.set(uid);
    }

    public static Long getUid() {
        return UID.get();
    }

    public static Long getUidOrNull() {
        return UID.get();
    }

    public static void clear() {
        UID.remove();
    }

    private UserContext() {
    }
}
