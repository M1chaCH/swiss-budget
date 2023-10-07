package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;

public class UnexpectedServerException extends AppException {

    public UnexpectedServerException(String message, Exception root) {
        super(true, message, Status.INTERNAL_SERVER_ERROR, root, null);
    }
}
