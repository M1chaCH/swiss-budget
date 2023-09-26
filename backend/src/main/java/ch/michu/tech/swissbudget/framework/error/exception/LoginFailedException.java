package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;

public class LoginFailedException extends AppException {

    public LoginFailedException(String username) {
        super(String.format("login failed for user %s", username), Status.FORBIDDEN);
    }
}
