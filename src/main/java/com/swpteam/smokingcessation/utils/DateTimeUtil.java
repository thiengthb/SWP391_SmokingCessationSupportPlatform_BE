package com.swpteam.smokingcessation.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DateTimeUtil {

    public final String FORMAT_WITH_MILLISECONDS = "yyyy-MM-dd HH:mm:ss.SSS";
    public final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public final String FORMAT_DATE = "yyyy-MM-dd";

    public final DateTimeFormatter FORMATTER_WITH_MILLISECONDS =
            DateTimeFormatter.ofPattern(FORMAT_WITH_MILLISECONDS);

    public final DateTimeFormatter FORMATTER_DATE_TIME =
            DateTimeFormatter.ofPattern(FORMAT_DATE_TIME);

    public final DateTimeFormatter FORMATTER_DATE =
            DateTimeFormatter.ofPattern(FORMAT_DATE);

    // Reformats

    public LocalDate reformat(LocalDate date) {
        return reformat(date, FORMATTER_DATE);
    }

    public LocalDate reformat(LocalDate date, DateTimeFormatter formatter) {
        if (date == null) return null;
        String formatted = formatter.format(date);
        return LocalDate.parse(formatted, formatter);
    }

    public LocalDateTime reformat(LocalDateTime dateTime) {
        return reformat(dateTime, FORMATTER_DATE_TIME);
    }

    public LocalDateTime reformat(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) return null;
        String formatted = formatter.format(dateTime);
        return LocalDateTime.parse(formatted, formatter);
    }

    // Formatters

    public String format(LocalDate date) {
        return Optional.ofNullable(date)
                .map(FORMATTER_DATE::format)
                .orElse(null);
    }

    public String format(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(FORMATTER_DATE_TIME::format)
                .orElse(null);
    }

    public String formatWithMillis(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(FORMATTER_WITH_MILLISECONDS::format)
                .orElse(null);
    }

    public String format(LocalDateTime dateTime, String pattern) {
        return Optional.ofNullable(dateTime)
                .map(dt -> dt.format(DateTimeFormatter.ofPattern(pattern)))
                .orElse(null);
    }

    // Parsers

    public LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, FORMATTER_DATE_TIME);
    }

    public LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        return LocalDate.parse(dateStr, FORMATTER_DATE);
    }

    public LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    // Comparisons

    public boolean isAfter(LocalDateTime a, LocalDateTime b) {
        return a != null && b != null && a.isAfter(b);
    }

    public boolean isBefore(LocalDateTime a, LocalDateTime b) {
        return a != null && b != null && a.isBefore(b);
    }
}
