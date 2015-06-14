package obrien.dao;

import io.dropwizard.hibernate.AbstractDAO;
import obrien.entity.TradeMessage;
import obrien.processing.Trends;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Dao class for trade messages.
 */
public class TradeDao extends AbstractDAO<TradeMessage> implements Dao<TradeMessage> {
    private static final Logger log = LoggerFactory.getLogger(TradeDao.class);
    private final SessionFactory sessionFactory;

    // Cached thread pool with max size 100 threads
    private static ExecutorService pool = new ThreadPoolExecutor(
            0, 100, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    public TradeDao(SessionFactory sessionFactory) {
        super(sessionFactory);
        // We are breaking dropwizards pattern to access directly
        this.sessionFactory = sessionFactory;
    }

    /**
     * Return immediately, we will persist the trade and extract metrics on another thread.
     * <p>We cant use dropwizards @UnitOfWork annotation as we are doing this on another thread, so we
     * get the session and transaction manually.</p>
     * @param trade the trade to be persisted and metrics extracted.
     */
    public void insert(TradeMessage trade) {
        pool.submit(() -> {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            Integer id = (Integer) session.save(trade);
            trade.setId(id);
            tx.commit();
            session.close();
            log.info("stored a trade for userId: {}", trade.getUserId());
            Trends.submitTrade(trade);
        });
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
