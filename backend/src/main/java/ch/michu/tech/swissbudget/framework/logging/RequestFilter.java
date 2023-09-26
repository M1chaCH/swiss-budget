package ch.michu.tech.swissbudget.framework.logging;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

@Logged
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class RequestFilter implements ContainerRequestFilter {

    public static final String REQUEST_START_TIME = "request_start";
    private final Logger logger = Logger.getLogger(RequestFilter.class.getSimpleName());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        MultivaluedMap<String, String> queries = requestContext.getUriInfo().getQueryParameters();
        StringJoiner queryString = new StringJoiner(",");
        queries.forEach((query, values) -> {
            StringJoiner valueString = new StringJoiner(",");
            values.forEach(valueString::add);
            queryString.add(String.format("%s=%s", query, valueString));
        });

        requestContext.setProperty(REQUEST_START_TIME, Instant.now());

        logger.log(Level.INFO, "received request to {0}:{1}?{2}",
            new Object[]{requestContext.getMethod(),
                requestContext.getUriInfo().getAbsolutePath().getPath(), queryString});
    }
}
