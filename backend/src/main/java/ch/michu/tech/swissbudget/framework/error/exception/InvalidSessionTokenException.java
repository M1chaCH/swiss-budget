package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;

public class InvalidSessionTokenException extends AppException {

    public InvalidSessionTokenException() {
        super("got invalid session token", Status.UNAUTHORIZED);
    }
}
