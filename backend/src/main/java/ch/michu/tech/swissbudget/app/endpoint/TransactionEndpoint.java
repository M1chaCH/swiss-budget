package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.service.TransactionService;
import ch.michu.tech.swissbudget.framework.authentication.Authenticated;
import ch.michu.tech.swissbudget.framework.logging.LoggedRequest;
import ch.michu.tech.swissbudget.framework.validation.ValidateDtos;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/transaction")
@RequestScoped
@LoggedRequest
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
    public Response getTransactions() {
        return Response.status(Status.OK).entity(service.getTransactions()).build();
    }

    @GET
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportTransactions() {
        return Response.status(Status.OK).entity(service.importTransactions()).build();
    }
}
