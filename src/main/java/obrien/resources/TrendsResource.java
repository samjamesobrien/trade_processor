package obrien.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import obrien.websockets.TrendsSocket;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Expose trends to a websocket.
 */
@Path("/trends")
public class TrendsResource {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @POST
    @Consumes("application/json")
    public void broadcast(Object data) throws Exception {
        TrendsSocket.broadcast(objectMapper.writeValueAsString(data));
    }

    @POST
    @Consumes("text/plain")
    public void broadcastString(String data) throws Exception {
        TrendsSocket.broadcast("Client message: " + data);
    }
}
