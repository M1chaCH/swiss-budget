package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.dto.CreateMailFolderDto;
import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.dto.RegisterDto;
import ch.michu.tech.swissbudget.app.service.RegistrationService;
import ch.michu.tech.swissbudget.framework.logging.LoggedRequest;
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
import java.util.List;

@Path("/register")
@RequestScoped
@LoggedRequest
@ValidateDtos
public class RegisterEndpoint {

    private final RegistrationService service;

    @Inject
    public RegisterEndpoint(RegistrationService service) {
        this.service = service;
    }

    @GET
    @Path("/bank")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSupportedBanks() {
        List<SupportedBankDto> banks = service.getSupportedBanks().stream().map(SupportedBankDto::new).toList();
        return Response.status(Status.OK).entity(banks).build();
    }

    @POST()
    @Path("/mail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postEmailCredentials(CredentialDto credentials) {
        service.checkMailCredentials(credentials);
        return Response.status(Status.NO_CONTENT).build();
    }

    @POST()
    @Path("/mail/folder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postEmailFolder(CreateMailFolderDto dto) {
        service.createMailFolder(dto);
        return Response.status(Status.NO_CONTENT).build();
    }

    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postRegister(RegisterDto dto) {
        return Response.status(Status.OK).entity(service.register(dto))
            .build();
    }

    public record SupportedBankDto(String key) {

    }
}
