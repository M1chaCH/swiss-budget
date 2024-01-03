package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;

public class InvalidInputException extends AppException {

    public InvalidInputException(String inputName, Object value) {
        super("got invalid input: %s -> %s".formatted(inputName, value), Status.BAD_REQUEST);
    }
}
