package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;

public class LoginFromNewClientException extends AppException {

    public LoginFromNewClientException() {
        super("session id expired due to login on new client", Status.UNAUTHORIZED);
    }
}
