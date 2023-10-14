package ch.michu.tech.swissbudget.app.exception;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;

public class ProcessAlreadyStartedException extends AppException {

    public ProcessAlreadyStartedException(String process) {
        super("process: %s, is already started".formatted(process), Status.BAD_REQUEST);
    }
}
