package obrien.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Expose trends to a websocket.
 */
@Path("/trends")
public class TrendsResource {

    @GET
    public Response trend() {
            return Response.status(200).build();

    }
}
