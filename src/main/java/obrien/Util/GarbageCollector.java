package obrien.Util;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clear out old entries in the DB.
 * <p>I don't really care about storing everything we persist, and we do have a hard limit with the cheaper DB
 * available on Heroku.</p>
 */
public class GarbageCollector {
    private static final Logger log = LoggerFactory.getLogger(GarbageCollector.class);
    private final Timer timer;
    private final TimerTask timerTask;

    private final SessionFactory sessionFactory;

    /**
     * While this instance exists, perform the task for the given frequency.
     * @param frequency how long in milliseconds between executions of the task.
     * @param sessionFactory the sessionFactory for hibernate.
     */
    public GarbageCollector(long frequency, SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

        timer = new Timer();
        timerTask = new TimerTask () {
            @Override
            public void run () {
                clearOldEntries();
            }
        };
        timer.schedule(timerTask, 0, frequency);
    }

    /**
     * Delete entries older than 30 minutes.
     */
    private void clearOldEntries() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Calendar future = Calendar.getInstance();
        future.add(Calendar.MINUTE, +10);   // aloow some leeway

        Calendar past = Calendar.getInstance();
        past.add(Calendar.MINUTE, -30);

        Query query = session.createQuery("DELETE FROM TradeMessage where timePlaced < :past  OR timePlaced > :future");
        query.setCalendarDate("past", past);
        query.setCalendarDate("future", future);
        int deleted = query.executeUpdate();

        tx.commit();
        session.close();
        log.info(" * * * Garbage collection occurred * * * deleted: {} entries", deleted);
    }
}
