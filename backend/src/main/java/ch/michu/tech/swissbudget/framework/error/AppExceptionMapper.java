package ch.michu.tech.swissbudget.framework.error;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@ApplicationScoped
public class AppExceptionMapper implements ExceptionMapper<AppException> {

    private static final Logger LOGGER = Logger.getLogger(AppExceptionMapper.class.getSimpleName());

    @Override
    public Response toResponse(AppException exception) {
        if (exception.isServerError()) {
            LOGGER.log(Level.SEVERE, "Server error!", exception);
        } else {
            Exception e =
                exception.getRootException() == null ? exception : exception.getRootException();

            LOGGER.log(Level.INFO, "caught exception --> {0}: {1}",
                new Object[]{e.getClass().getSimpleName(), exception.getServerMessage()});
        }

        return exception.buildResponse();
    }
}
