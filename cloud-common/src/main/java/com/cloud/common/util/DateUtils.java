package com.cloud.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具。
 */
public final class DateUtils {

    private static final DateTimeFormatter DEFAULT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private DateUtils() {
    }

    public static String formatNow() {
        return DEFAULT_FMT.format(Instant.now());
    }

    public static String format(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.atZone(ZoneId.systemDefault()).format(DEFAULT_FMT);
    }
}
