package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.dto.transaction.TransactionDto;
import ch.michu.tech.swissbudget.app.service.TransactionService;
import ch.michu.tech.swissbudget.framework.LocalDateDeserializer;
import ch.michu.tech.swissbudget.framework.authentication.Authenticated;
import ch.michu.tech.swissbudget.framework.logging.LoggedRequest;
import ch.michu.tech.swissbudget.framework.validation.ValidateDtos;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDate;
import java.util.Arrays;

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
    public Response getTransactions(
        @QueryParam("query") @DefaultValue("") String query,
        @QueryParam("tagIds") @DefaultValue("") String tagIds,
        @QueryParam("from") @DefaultValue("") String fromDate,
        @QueryParam("to") @DefaultValue("") String toDate,
        @QueryParam("needAttention") @DefaultValue("false") boolean needAttention,
        @QueryParam("page") @DefaultValue("1") int page
    ) {
        LocalDate from = null;
        if (!fromDate.isBlank()) {
            from = LocalDateDeserializer.parseLocalDate(fromDate);
        }
        LocalDate to = null;
        if (!toDate.isBlank()) {
            to = LocalDateDeserializer.parseLocalDate(toDate);
        }
        int[] tags = Arrays.stream(tagIds.split(";")).filter(s -> !s.isBlank()).mapToInt(Integer::parseInt).toArray();

        return Response.status(Status.OK).entity(service.getTransactions(query, tags, from, to, needAttention, page)).build();
    }

    @GET
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportTransactions() {
        return Response.status(Status.OK).entity(service.importTransactions()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putTransaction(TransactionDto toUpdate) {
        service.updateTransaction(toUpdate);
        return Response.status(Status.NO_CONTENT).build();
    }
}
