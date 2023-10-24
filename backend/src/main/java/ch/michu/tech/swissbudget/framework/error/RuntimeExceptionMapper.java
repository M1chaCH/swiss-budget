package ch.michu.tech.swissbudget.framework.error;

import ch.michu.tech.swissbudget.app.exception.UnexpectedDbException;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.dto.ErrorDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import org.jooq.exception.DataAccessException;

@Provider
@RequestScoped
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private final ErrorReporter errorReporter;
    private final RequestSupport support;

    @Inject
    public RuntimeExceptionMapper(ErrorReporter errorReporter, RequestSupport support) {
        this.errorReporter = errorReporter;
        this.support = support;
    }

    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof WebApplicationException webException) {
            support.logInfo(this, "caught web exception: %s: %s",
                exception.getClass().getSimpleName(), exception.getMessage());
            return webException.getResponse();
        } else if (exception instanceof ProcessingException) {
            String exceptionName = exception.getClass().getSimpleName();
            String causeName = exception.getCause().getClass().getSimpleName();
            support.logInfo(this, "caught %s - %s: %s", exceptionName, causeName, exception.getMessage());

            return Response.status(Status.BAD_REQUEST)
                .entity(new ErrorDto(exceptionName, Map.of("cause", causeName, "message", exception.getMessage())))
                .build();
        } else if (exception instanceof DataAccessException dataAccessException) {
            UnexpectedDbException dbException = new UnexpectedDbException(dataAccessException);
            support.logWarning(this, dbException.getServerMessage(), dataAccessException);
            errorReporter.reportError(exception);
            return dbException.buildResponse();
        }

        errorReporter.reportError(exception);
        support.logWarning(this, "RuntimeException: %s: %s", exception,
            exception.getClass().getSimpleName(), exception.getMessage());
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}