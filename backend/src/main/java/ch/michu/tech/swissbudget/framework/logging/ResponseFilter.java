package ch.michu.tech.swissbudget.framework.logging;

import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.Optional;

@LoggedRequest
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class ResponseFilter implements ContainerResponseFilter {

    @Inject
    private jakarta.inject.Provider<RequestSupport> supportProvider;

    @Override
    public void filter(ContainerRequestContext requestContext,
        ContainerResponseContext responseContext) {
        RequestSupport support = supportProvider.get();
        Optional<Instant> startTime = support.loadProperty(RequestFilter.REQUEST_START_TIME,
            Instant.class);
        long duration = startTime.map(
                instant -> Instant.now().toEpochMilli() - instant.toEpochMilli())
            .orElse(-1L);

        support.logInfo(this, "<- outgoing %s:%s (%s - %s) took %sms",
            requestContext.getMethod(),
            requestContext.getUriInfo().getAbsolutePath().getPath(),
            responseContext.getStatus(),
            responseContext.getStatusInfo().getReasonPhrase(),
            duration);
    }
}
