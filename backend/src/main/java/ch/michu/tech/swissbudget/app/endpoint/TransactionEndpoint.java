package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.service.TransactionService;
import ch.michu.tech.swissbudget.framework.authentication.Authenticated;
import ch.michu.tech.swissbudget.framework.logging.Logged;
import ch.michu.tech.swissbudget.framework.validation.ValidateDtos;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/transaction")
@RequestScoped
@Logged
@ValidateDtos
@Authenticated
public class TransactionEndpoint {

    private final TransactionService service;

    @Inject
    public TransactionEndpoint(TransactionService service) {
        this.service = service;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(@DefaultValue("true") @QueryParam("import") boolean includeImport) {
        return Response.status(Status.OK).entity(service.getTransactions(includeImport)).build();
    }
}
