package obrien.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import obrien.entity.TradeMessage;
import obrien.processing.Trends;
import obrien.websockets.TrendsSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Exposes the calculated trends to normal calls and pushed to a web socket.
 * <p>We don't want to calculate the trends for every trade, but push their updated state intermittently.</p>
 */
@Path("/trends")
public class TrendsResource {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(Trends.class);
    private static final Timer pushTotals;
    private static final TimerTask pushTrends;

    static {
        pushTotals = new Timer();
        pushTrends = new TimerTask () {
            @Override
            public void run () {
                publishTrends();
            }
        };
        pushTotals.schedule(pushTrends, 0, 500);
    }

    /**
     * Get the totals and push them to the websocket.
     */
    private static void publishTrends() {
        Map totals = Trends.calculateTotals();
        try {
            String output = MAPPER.writeValueAsString(totals);
            TrendsSocket.broadcast(output);
            LOG.debug("pushed totals to socket, {}", totals);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize a trade message", e);
        }
    }

    /**
     * Manually call get totals.
     * @return calculated totals.
     */
    @GET
    public Map getTotals() {
        return Trends.calculateTotals();
    }

    /**
     * Send arbitrary data to the trends web socket.
     * @param data string to be sent to teh socket.
     */
    @POST
    @Consumes("text/plain")
    public void broadcastString(String data) {
        TrendsSocket.broadcast(data);
    }
}
