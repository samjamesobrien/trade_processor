package obrien.resources;

import com.google.common.util.concurrent.RateLimiter;
import io.dropwizard.hibernate.UnitOfWork;
import obrien.Util.RateLimitProvider;
import obrien.configuration.AppConfiguration;
import obrien.dao.Dao;
import obrien.dao.TradeDao;
import obrien.entity.TradeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static obrien.Util.DefaultValues.MAX_WAIT_MILLIS;

/**
 * Trade API resource.
 */
@Path("/trade")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TradeResource {
    private static final Logger LOG = LoggerFactory.getLogger(TradeResource.class);

    private static int rateLimit;
    private static String TOO_MANY_REQUESTS;
    private static RateLimitProvider rlp;

    private final Dao<TradeMessage> dao;

    public TradeResource(AppConfiguration config, TradeDao dao, RateLimitProvider rateLimitProvider) {
        this.dao = dao;
        if (rateLimit == 0 || TOO_MANY_REQUESTS == null || rlp == null) {
            rateLimit = config.getRateLimit();
            rlp = rateLimitProvider;
            TOO_MANY_REQUESTS = "Too many requests per second for this user, " + rateLimit + " are allowed";
        }
    }

    @UnitOfWork
    @POST
    public Response submitMessage(TradeMessage tradeMessage) {
        RateLimiter rl = rlp.getRateLimiter(tradeMessage.getUserId());
        if (rl.tryAcquire(100, TimeUnit.MILLISECONDS)) {
            dao.insert(tradeMessage);
            LOG.debug("User: {} registered a trade", tradeMessage.getUserId());
            return Response.status(201).build();
        } else {
            LOG.info("User: {} exceeded their request rate", tradeMessage.getUserId());
            return Response.status(429).entity(TOO_MANY_REQUESTS).build();
        }
    }

    @UnitOfWork(readOnly = true)
    @GET
    public Response getAllUsersMessages(int userId) {
        RateLimiter rl = rlp.getRateLimiter(userId);
        if (rl.tryAcquire(MAX_WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
            LOG.debug("User: {} requested their trades", userId);
            return Response.ok().entity(dao.retrieveAll(userId)).build();
        } else {
            LOG.info("User: {} exceeded their request rate", userId);
            return Response.status(429).entity(TOO_MANY_REQUESTS).build();
        }
    }
}
