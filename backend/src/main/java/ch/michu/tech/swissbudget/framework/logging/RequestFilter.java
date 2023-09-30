package ch.michu.tech.swissbudget.framework.logging;

import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.StringJoiner;

@Logged
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class RequestFilter implements ContainerRequestFilter {

    public static final String REQUEST_START_TIME = "request_start";

    @Inject
    private jakarta.inject.Provider<RequestSupport> supportProvider;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        MultivaluedMap<String, String> queries = requestContext.getUriInfo().getQueryParameters();
        StringJoiner queryString = new StringJoiner(",");
        queries.forEach((query, values) -> {
            StringJoiner valueString = new StringJoiner(",");
            values.forEach(valueString::add);
            queryString.add(String.format("%s=%s", query, valueString));
        });

        RequestSupport support = supportProvider.get();
        support.storeProperty(REQUEST_START_TIME, Instant.now());
        support.logInfo(this, "-> incoming %s:%s?%s", requestContext.getMethod(),
            requestContext.getUriInfo().getAbsolutePath().getPath(), queryString);
    }
}
