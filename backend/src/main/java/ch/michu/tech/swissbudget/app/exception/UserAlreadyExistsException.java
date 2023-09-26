package ch.michu.tech.swissbudget.app.exception;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;

public class UserAlreadyExistsException extends AppException {

    public UserAlreadyExistsException(String mail) {
        super(String.format("user with mail %s already exists", mail), Status.BAD_REQUEST);
    }
}
