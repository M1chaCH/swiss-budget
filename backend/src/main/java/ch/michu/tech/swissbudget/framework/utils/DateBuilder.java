package ch.michu.tech.swissbudget.framework.utils;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateBuilder {

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    protected LocalDate current = LocalDate.MIN;

    protected DateBuilder(LocalDate date) {
        this.current = date;
    }

    public static DateBuilder today() {
        LocalDate current = LocalDate.now(ZoneId.of("CET"));
        return new DateBuilder(current);
    }

    public static DateBuilder from(LocalDate date) {
        return new DateBuilder(date);
    }

    public DateBuilder addDays(int amount) {
        current = current.plusDays(amount);
        return this;
    }

    public DateBuilder addWeeks(int amount) {
        current = current.plusWeeks(amount);
        return this;
    }

    public DateBuilder addMonths(int amount) {
        current = current.plusMonths(amount);
        return this;
    }

    public DateBuilder addYears(int amount) {
        current = current.plusYears(amount);
        return this;
    }

    public DateBuilder firstDayOfMonth() {
        current = current.withDayOfMonth(1);
        return this;
    }

    public DateBuilder lastDayOfMonth() {
        YearMonth yearMonth = YearMonth.of(current.getYear(), current.getMonth());
        current = current.withDayOfMonth(yearMonth.lengthOfMonth());
        return this;
    }

    public DateBuilder firstDayOfYear() {
        current = current.withDayOfYear(1);
        return this;
    }

    public DateBuilder lastDayOfYear() {
        Year year = Year.of(current.getYear());
        current = current.withDayOfYear(year.length());
        return this;
    }

    public String formatted() {
        return current.format(DEFAULT_DATE_FORMATTER);
    }

    public String formatted(String pattern) {
        return current.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String formatted(DateTimeFormatter formatter) {
        return current.format(formatter);
    }
}
