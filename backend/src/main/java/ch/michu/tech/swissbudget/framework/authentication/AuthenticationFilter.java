package ch.michu.tech.swissbudget.framework.authentication;

import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.InvalidSessionTokenException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@RequestScoped
@Authenticated
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private final AuthenticationService authService;
    private final RequestSupport support;

    @Inject
    public AuthenticationFilter(AuthenticationService authService, RequestSupport support) {
        this.authService = authService;
        this.support = support;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String jwt = requestContext.getHeaderString(AuthenticationService.HEADER_AUTH_TOKEN);
        if (jwt == null || jwt.isBlank()) {
            throw new InvalidSessionTokenException();
        }

        SessionToken token = authService.validateToken(jwt);
        support.setSessionToken(token);

        support.logFine(this, "granted access");
    }
}
