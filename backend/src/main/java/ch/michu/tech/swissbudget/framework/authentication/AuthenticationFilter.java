package ch.michu.tech.swissbudget.framework.authentication;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Authenticated
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(
        AuthenticationFilter.class.getSimpleName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.log(Level.INFO, "authenticating user", new Object[]{}); // TODO implement
    }
}
