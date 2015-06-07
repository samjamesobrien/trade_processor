package obrien.dao;

import obrien.entity.TradeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Dao class for trade messages.
 */
public class TradeDao implements Dao<TradeMessage> {

    private static final Logger log = LoggerFactory.getLogger(TradeDao.class);


    public void insert(TradeMessage trade) {
        log.info("stored a trade");
    }

    public List<TradeMessage> retrieveAll(int userId) {
        log.info("retrieved a lot of trades for user: {}", userId);

        return new ArrayList<>();
    }
}
