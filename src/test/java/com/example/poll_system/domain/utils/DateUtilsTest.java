package com.example.poll_system.domain.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DateUtilsTest {

    @Test
    void shouldIntanceDateUtils() {
        DateUtils dateUtils = new DateUtils();
        assertTrue(dateUtils instanceof DateUtils);
    }

    @Test
    void isDateInFuture_ShouldReturnTrue_WhenDateIsAfterNow() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        assertTrue(DateUtils.isDateInFuture(futureDate));
    }

    @Test
    void isDateInFuture_ShouldReturnFalse_WhenDateIsBeforeNow() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        assertFalse(DateUtils.isDateInFuture(pastDate));
    }

    @Test
    void isDateInFuture_ShouldReturnFalse_WhenDateIsNow() {
        LocalDateTime now = LocalDateTime.now();
        assertFalse(DateUtils.isDateInFuture(now));
    }

    @Test
    void isDateInFuture_ShouldReturnFalse_WhenDateIsNull() {
        assertFalse(DateUtils.isDateInFuture(null));
    }

    @Test
    void isDateInPast_ShouldReturnTrue_WhenDateIsBeforeNow() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        assertTrue(DateUtils.isDateInPast(pastDate));
    }

    @Test
    void isDateInPast_ShouldReturnFalse_WhenDateIsAfterNow() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        assertFalse(DateUtils.isDateInPast(futureDate));
    }

    @Test
    void isDateInPast_ShouldReturnFalse_WhenDateIsNull() {
        assertFalse(DateUtils.isDateInPast(null));
    }

    @Test
    void isDateInRange_ShouldReturnTrue_WhenStartIsBeforeEnd() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        assertTrue(DateUtils.isDateInRange(start, end));
    }

    @Test
    void isDateInRange_ShouldReturnFalse_WhenStartIsAfterEnd() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.plusDays(1);
        assertFalse(DateUtils.isDateInRange(start, end));
    }

    @Test
    void isDateInRange_ShouldReturnFalse_WhenStartEqualsEnd() {
        LocalDateTime date = LocalDateTime.now();
        assertFalse(DateUtils.isDateInRange(date, date));
    }

    @Test
    void isDateInRange_ShouldReturnFalse_WhenStartIsNull() {
        LocalDateTime end = LocalDateTime.now();
        assertFalse(DateUtils.isDateInRange(null, end));
    }

    @Test
    void isDateInRange_ShouldReturnFalse_WhenEndIsNull() {
        LocalDateTime start = LocalDateTime.now();
        assertFalse(DateUtils.isDateInRange(start, null));
    }

    @Test
    void isDateInRange_ShouldReturnFalse_WhenBothAreNull() {
        assertFalse(DateUtils.isDateInRange(null, null));
    }
}
