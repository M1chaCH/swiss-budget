package ch.michu.tech.swissbudget.framework.error;

import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@RequestScoped
public class AppExceptionMapper implements ExceptionMapper<AppException> {

    private final RequestSupport support;
    private final ErrorReporter errorReporter;

    @Inject
    public AppExceptionMapper(RequestSupport support,
        ErrorReporter errorReporter) {
        this.support = support;
        this.errorReporter = errorReporter;
    }

    @Override
    public Response toResponse(AppException exception) {
        if (exception.isServerError()) {
            support.logError(this, "Server error!", exception);
            errorReporter.reportError(exception);
        } else {
            Exception e = exception.getRootException() == null ? exception : exception.getRootException();

            support.logInfo(this, "caught exception -> %s: %s", e.getClass().getSimpleName(), exception.getServerMessage());

            if (exception.isReportToAdmin()) {
                errorReporter.reportError(exception);
            }
        }

        return exception.buildResponse();
    }
}
