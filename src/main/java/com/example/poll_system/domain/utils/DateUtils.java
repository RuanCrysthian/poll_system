package com.example.poll_system.domain.utils;

import java.time.LocalDateTime;

public class DateUtils {

    public static boolean isDateInFuture(LocalDateTime date) {
        return date != null && date.isAfter(LocalDateTime.now());
    }

    public static boolean isDateInPast(LocalDateTime date) {
        return date != null && date.isBefore(LocalDateTime.now());
    }

    public static boolean isDateInRange(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null && start.isBefore(end);
    }

}
