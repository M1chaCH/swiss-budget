package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;
import java.lang.reflect.Field;
import java.util.Map;
import lombok.Getter;

@Getter
public class DtoValidationException extends AppException {

    private final Object dto;
    private final Field field;
    private final Class<?> validator;

    public DtoValidationException(Object dto, Field field, Class<?> validator) {
        super(String.format("%s failed for %s->%s", validator.getSimpleName(),
            dto.getClass().getSimpleName(), field.getName()), Status.BAD_REQUEST, Map.of(
            "dto", dto.getClass().getSimpleName(),
            "field", field.getName(),
            "validator", validator.getSimpleName()
        ));

        this.dto = dto;
        this.field = field;
        this.validator = validator;
    }
}
