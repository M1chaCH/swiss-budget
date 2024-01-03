package ch.michu.tech.swissbudget.app.endpoint;

import ch.michu.tech.swissbudget.app.dto.tag.AssignTagDto;
import ch.michu.tech.swissbudget.app.dto.tag.ResolveConflictDto;
import ch.michu.tech.swissbudget.app.dto.tag.UpdateTagDto;
import ch.michu.tech.swissbudget.app.service.TagService;
import ch.michu.tech.swissbudget.framework.authentication.Authenticated;
import ch.michu.tech.swissbudget.framework.logging.LoggedRequest;
import ch.michu.tech.swissbudget.framework.validation.ValidateDtos;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.UUID;

@Path("/tag")
@RequestScoped
@LoggedRequest
@ValidateDtos
@Authenticated
public class TagEndpoint {

    private final TagService service;

    @Inject
    public TagEndpoint(TagService service) {
        this.service = service;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTags() {
        return Response.status(Status.OK).entity(service.getTags()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postTag(UpdateTagDto dto) {
        service.createTag(dto);
        return Response.status(Status.CREATED).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putTag(UpdateTagDto dto) {
        service.updateTag(dto);
        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{tagId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTag(@PathParam("tagId") UUID tagId) {
        service.deleteTag(tagId);
        return Response.status(Status.NO_CONTENT).build();
    }

    @POST
    @Path("/validate_no_keyword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postValidateKeyword(
        @QueryParam("keyword") String keyword
    ) {
        service.validateNewKeyword(keyword);
        return Response.status(Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/assign_tag")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putAssignTag(AssignTagDto dto) {
        service.assignTag(dto.getTransactionId(), dto.getTagId(), dto.getKeyword());
        return Response.status(Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/resolve_conflict")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putResolveConflicts(ResolveConflictDto dto) {
        service.resolveConflict(dto.getTransactionId(), dto.getSelectedTagId(), dto.getMatchingKeywordId(),
            dto.isRemoveUnselectedKeywords());
        return Response.status(Status.NO_CONTENT).build();
    }
}
