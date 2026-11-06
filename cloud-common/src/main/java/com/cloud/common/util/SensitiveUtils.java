package com.cloud.common.util;

/**
 * 敏感信息脱敏（日志输出前可调用）。
 */
public final class SensitiveUtils {

    private SensitiveUtils() {
    }

    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 7) {
            return "****";
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    public static String maskIdCard(String id) {
        if (id == null || id.length() < 8) {
            return "****";
        }
        return id.substring(0, 4) + "**********" + id.substring(id.length() - 4);
    }
}
