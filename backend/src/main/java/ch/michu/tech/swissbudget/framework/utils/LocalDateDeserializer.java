package ch.michu.tech.swissbudget.framework.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalDateDeserializer {

    public static final String DEFAULT_LOCAL_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    public static final String FALLBACK_LOCAL_DATE_PATTERN = "yyyy-MM-dd";
    private static final Logger LOGGER = Logger.getLogger(LocalDateDeserializer.class.getSimpleName());

    private LocalDateDeserializer() {
    }

    public static LocalDate parseLocalDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_PATTERN));
        } catch (DateTimeParseException e) {
            try {
                LOGGER.log(Level.FINE,
                           "custom LocalDate deserializer WARN: could not parse date (string:{0} - pattern:{1}), using ISO / fallback {2}",
                           new Object[]{dateString, DEFAULT_LOCAL_DATE_PATTERN, FALLBACK_LOCAL_DATE_PATTERN});
                return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e2) {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(FALLBACK_LOCAL_DATE_PATTERN));
            }
        }
    }
}
