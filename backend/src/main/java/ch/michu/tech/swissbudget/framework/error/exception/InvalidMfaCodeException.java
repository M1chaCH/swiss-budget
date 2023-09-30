package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;

public class InvalidMfaCodeException extends AppException {

    public InvalidMfaCodeException() {
        super("got invalid mfa code", Status.UNAUTHORIZED);
    }
}
