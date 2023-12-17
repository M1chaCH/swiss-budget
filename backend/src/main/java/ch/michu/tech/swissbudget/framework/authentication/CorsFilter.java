package ch.michu.tech.swissbudget.framework.authentication;

import io.helidon.http.HeaderNames;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    private final String corsOrigin;

    @Inject
    public CorsFilter(
        @ConfigProperty(name = "server.cors.origin", defaultValue = "localhost") String corsOrigin
    ) {
        this.corsOrigin = corsOrigin;
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        response.getHeaders().add("Access-Control-Allow-Origin", corsOrigin);
        response.getHeaders().add("Access-Control-Allow-Headers", "%s, %s, %s, %s".formatted(
            AuthenticationService.HEADER_AUTH_TOKEN,
            AuthenticationService.HEADER_X_REAL_IP.defaultCase(),
            HeaderNames.X_FORWARDED_FOR.defaultCase(),
            HeaderNames.CONTENT_TYPE.defaultCase()));
        response.getHeaders().add("Access-Control-Allow-Credentials", "false");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.getHeaders().add("Access-Control-Max-Age", "518400"); // 6 Days in seconds
    }
}
