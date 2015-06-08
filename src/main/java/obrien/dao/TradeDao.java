package obrien.dao;

import io.dropwizard.hibernate.AbstractDAO;
import obrien.entity.TradeMessage;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Dao class for trade messages.
 */
public class TradeDao extends AbstractDAO<TradeMessage> implements Dao<TradeMessage> {
    private static final Logger log = LoggerFactory.getLogger(TradeDao.class);

    public TradeDao(SessionFactory sessionFactory) {
           super(sessionFactory);
    }

    public void insert(TradeMessage trade) {
        persist(trade);
        log.info("stored a trade for userId: {}", trade.getUserId());
    }

    public List<TradeMessage> retrieveAll(int userId) {
        Criteria c = currentSession()
                .createCriteria(TradeMessage.class)
                .add(Restrictions.eq("userId", userId))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        log.info("retrieved a list of trades for userId: {}", userId);
        return list(c);
    }
}
