package ch.michu.tech.swissbudget.framework.error.exception;

import ch.michu.tech.swissbudget.framework.dto.ErrorDto;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {

    protected final boolean serverError;
    protected final String serverMessage;
    protected final Status responseStatus;
    protected final Exception rootException;
    protected final transient Map<String, String> args;
    protected final String errorKey;

    protected AppException(String message, Status responseStatus) {
        this.serverError = false;
        this.serverMessage = message;
        this.responseStatus = responseStatus;
        this.rootException = this;
        this.args = new HashMap<>();
        this.errorKey = getClass().getSimpleName();
    }

    protected AppException(String serverMessage, Status responseStatus, Map<String, String> args) {
        this.serverError = false;
        this.serverMessage = serverMessage;
        this.responseStatus = responseStatus;
        this.rootException = this;
        this.args = args;
        this.errorKey = getClass().getSimpleName();
    }

    protected AppException(boolean serverError, String serverMessage,
        Status responseStatus, Exception rootException, Map<String, String> args) {
        super(serverMessage, rootException);
        this.serverError = serverError;
        this.serverMessage = serverMessage;
        this.responseStatus = responseStatus;
        this.rootException = rootException;
        this.args = args;
        this.errorKey = getClass().getSimpleName();
    }

    public Response buildResponse() {
        return Response.status(responseStatus).entity(getResponseBody()).build();
    }

    public Object getResponseBody() {
        return new ErrorDto(errorKey, args);
    }
}
