package ch.michu.tech.swissbudget.app.exception;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;
import java.util.Map;

public class TagAlreadyExistsException extends AppException {

    public TagAlreadyExistsException(String tagName) {
        super(
            "user tried to have two tags with the same name: %s".formatted(tagName),
            Status.BAD_REQUEST,
            Map.of("tag", tagName)
        );
    }
}
