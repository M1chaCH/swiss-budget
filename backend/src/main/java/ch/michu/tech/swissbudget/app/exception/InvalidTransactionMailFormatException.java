package ch.michu.tech.swissbudget.app.exception;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;

public class InvalidTransactionMailFormatException extends AppException {

    public InvalidTransactionMailFormatException(String bank, String message) {
        super(true, String.format("failed to parse transaction mail (bank:%s): %s", bank, message),
            Status.INTERNAL_SERVER_ERROR, null, null);
    }
}
