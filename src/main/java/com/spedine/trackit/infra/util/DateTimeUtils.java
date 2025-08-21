package com.spedine.trackit.infra.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateTimeUtils() {
    }

    public static String nowFormatted() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
