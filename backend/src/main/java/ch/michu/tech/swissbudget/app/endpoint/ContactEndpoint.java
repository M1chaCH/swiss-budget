package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.dto.ContactMessageDto;
import ch.michu.tech.swissbudget.app.service.mail.MailService;
import ch.michu.tech.swissbudget.framework.logging.LoggedRequest;
import ch.michu.tech.swissbudget.framework.validation.ValidateDtos;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/contact")
@RequestScoped
@LoggedRequest
@ValidateDtos
public class ContactEndpoint {

    private final MailService mailService;

    @Inject
    public ContactEndpoint(MailService mailService) {
        this.mailService = mailService;
    }

    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postContactMessage(ContactMessageDto questionDto) {
        mailService.sendUserMessageToAdmin(questionDto.getSourceAddress(), questionDto.getSubject(),
            questionDto.getMessage());
        return Response.status(Status.NO_CONTENT).build();
    }
}
