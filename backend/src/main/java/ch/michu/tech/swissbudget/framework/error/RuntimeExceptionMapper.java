package ch.michu.tech.swissbudget.framework.error;

import ch.michu.tech.swissbudget.framework.dto.MessageDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@ApplicationScoped
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = Logger.getLogger(
        RuntimeExceptionMapper.class.getSimpleName());

    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof WebApplicationException webException) {
            LOGGER.log(Level.INFO, "caught runtime exception: {0}: {1}",
                new Object[]{exception.getClass().getSimpleName(), exception.getMessage()});
            return webException.getResponse();
        } else if (exception instanceof ProcessingException) {
            LOGGER.log(Level.INFO, "could not parse request {0}",
                new Object[]{exception.getMessage()});
            return Response.status(Status.BAD_REQUEST)
                .entity(new MessageDto(String.format("invalid JSON %s", exception.getMessage())))
                .build();
        }

        LOGGER.log(Level.WARNING, "RuntimeException: {0}: {1}",
            new Object[]{exception.getClass().getSimpleName(), exception.getMessage()});
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}