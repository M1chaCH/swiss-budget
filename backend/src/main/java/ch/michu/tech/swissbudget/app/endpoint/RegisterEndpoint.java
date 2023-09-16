package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.service.RegistrationService;
import ch.michu.tech.swissbudget.framework.filter.logging.Logged;
import ch.michu.tech.swissbudget.framework.interceptor.validation.ValidateDtos;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/register")
@RequestScoped
@Logged
@ValidateDtos
public class RegisterEndpoint {

    private final RegistrationService service;

    @Inject
    public RegisterEndpoint(RegistrationService service) {
        this.service = service;
    }

    @POST()
    @Path("/mail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postEmailCredentials(CredentialDto credentials) {
        service.checkMailCredentials(credentials);
        return Response.status(Status.NO_CONTENT).build();
    }
}
