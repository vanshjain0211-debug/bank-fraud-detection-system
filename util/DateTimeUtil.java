package com.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static final DateTimeFormatter STANDARD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime time) {
        return time == null ? "-" : time.format(STANDARD_FORMAT);
    }

    public static boolean isWithinSeconds(LocalDateTime t1, LocalDateTime t2, long seconds) {
        if (t1 == null || t2 == null) return false;
        return Math.abs(Duration.between(t1, t2).getSeconds()) <= seconds;
    }
}
