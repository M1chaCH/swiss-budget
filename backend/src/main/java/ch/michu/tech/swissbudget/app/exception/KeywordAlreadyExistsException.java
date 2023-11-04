package ch.michu.tech.swissbudget.app.exception;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;
import java.util.Map;

public class KeywordAlreadyExistsException extends AppException {

    public KeywordAlreadyExistsException(String tagName, String keyword) {
        super(
            "found keyword when none was expected: %s-%s".formatted(tagName, keyword),
            Status.BAD_REQUEST,
            Map.of("tag", tagName, "keyword", keyword)
        );
    }
}
