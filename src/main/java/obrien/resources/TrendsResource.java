package obrien.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import obrien.entity.TradeMessage;
import obrien.websockets.TrendsSocket;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Exposes the websocket to arbitrary calls.
 * <p>This isn't a necessary resource but could be useful.</p>
 */
@Path("/trends")
public class TrendsResource {

    private final ObjectMapper mapper = new ObjectMapper();

    @POST
    @Consumes("application/json")
    public void broadcast(TradeMessage tradeMessage) throws JsonProcessingException {
        TrendsSocket.broadcast(mapper.writeValueAsString(tradeMessage));
    }

    @POST
    @Consumes("text/plain")
    public void broadcastString(String data) {
        TrendsSocket.broadcast(data);
    }
}
