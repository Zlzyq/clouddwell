package com.cloud.common.util;

import org.springframework.util.StringUtils;

/**
 * 简单字符串工具。
 */
public final class StringUtilsEx {

    private StringUtilsEx() {
    }

    public static boolean isBlank(String s) {
        return !StringUtils.hasText(s);
    }

    public static String emptyToNull(String s) {
        return StringUtils.hasText(s) ? s : null;
    }
}
