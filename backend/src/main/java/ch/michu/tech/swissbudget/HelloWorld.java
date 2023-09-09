
package ch.michu.tech.swissbudget;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
public class HelloWorld {

    /**
     * A nice little Hello World!
     *
     * @return the HTTP response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDefaultMessage() {
        return Response.status(Status.OK).entity("Hello World!").build();
    }

}
