package ch.michu.tech.swissbudget.framework.utils;

import ch.michu.tech.swissbudget.framework.error.exception.InvalidInputException;
import java.util.UUID;

public class ParsingUtils {

    private ParsingUtils() {
    }

    public static UUID[] toUUIDArray(Object... strings) {
        if (strings == null) {
            return new UUID[0];
        }

        try {
            UUID[] ids = new UUID[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String string = strings[i].toString();
                if (string.isBlank()) {
                    continue;
                }

                ids[i] = UUID.fromString(string);
            }

            return ids;
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new InvalidInputException("ID strings", strings);
        }
    }
}
