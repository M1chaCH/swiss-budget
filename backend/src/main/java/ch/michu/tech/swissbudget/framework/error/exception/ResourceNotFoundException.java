package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;
import java.util.Map;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String entity, String value) {
        super("no '%s' found by '%s'".formatted(entity, value), Status.NOT_FOUND, Map.of("entity", entity, "value", value));
    }
}
