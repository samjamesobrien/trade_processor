package obrien.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import obrien.entity.TradeMessage;
import obrien.websockets.TrendsSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Track overall metrics for trades.
 */
public class Trends {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(Trends.class);

    public static void submitTrade(TradeMessage tradeMessage) {
        try {
            TrendsSocket.broadcast(MAPPER.writeValueAsString(tradeMessage));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize a trade message for some reason", e);
        }
    }
}
