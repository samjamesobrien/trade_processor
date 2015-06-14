package obrien.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Serves up the index page.
 */
@Path("/")
public class IndexResource {
    private static final Logger LOG = LoggerFactory.getLogger(IndexResource.class);
    private String index;


    /**
     * Returns the index page.
     * @return index.html page.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response index() {
        return Response.status(200).entity("index").build();
    }
}
