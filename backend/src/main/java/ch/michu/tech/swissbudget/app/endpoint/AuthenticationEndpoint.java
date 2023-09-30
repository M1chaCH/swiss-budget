package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.dto.LoginDto;
import ch.michu.tech.swissbudget.app.dto.MfaCodeDto;
import ch.michu.tech.swissbudget.framework.authentication.Authenticated;
import ch.michu.tech.swissbudget.framework.authentication.AuthenticationService;
import ch.michu.tech.swissbudget.framework.dto.MessageDto;
import ch.michu.tech.swissbudget.framework.logging.Logged;
import ch.michu.tech.swissbudget.framework.validation.ValidateDtos;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/auth")
@RequestScoped
@Logged
@ValidateDtos
public class AuthenticationEndpoint {

    private final AuthenticationService service;

    @Inject
    public AuthenticationEndpoint(AuthenticationService service) {
        this.service = service;
    }

    @GET
    @Authenticated
    public Response getValidatedToken() {
        return Response.status(Status.NO_CONTENT).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postLogin(LoginDto dto) {
        String token = service.login(dto.getCredentials().getMail(),
            dto.getCredentials().getPassword(), dto.isStay());
        return Response.status(Status.OK).entity(new MessageDto(token)).build();
    }

    @POST
    @Path("/mfa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCheckMfaCode(MfaCodeDto mfaCodeDto) {
        String sessionToken = service.validateMfaCode(mfaCodeDto.getUserId(),
            mfaCodeDto.getProcessId(),
            mfaCodeDto.getCode());
        return Response.status(Status.OK).entity(new MessageDto(sessionToken)).build();
    }
}
