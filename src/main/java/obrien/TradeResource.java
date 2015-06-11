package obrien;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import io.dropwizard.hibernate.UnitOfWork;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Trade API resource.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TradeResource {
    private static final Logger LOG = LoggerFactory.getLogger(TradeResource.class);
    private static final int CACHE_SIZE = 10000;
    private static final int DEFAULT_RATE_LIMIT_SECONDS = 10;
    private static final int MAX_WAIT_MILLIS = 100;

    private static int rateLimit;
    private static Response TOO_MANY_REQUESTS;

    private final Dao<TradeMessage> dao;

    // We cache each users rate limiter
    private static final LoadingCache<Integer, RateLimiter> rateLimitCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(CACHE_SIZE)
            .build(new CacheLoader<Integer, RateLimiter>() {
                        public RateLimiter load(Integer userId) throws Exception {
                            int limit = rateLimit != 0 ? rateLimit : DEFAULT_RATE_LIMIT_SECONDS;
                            return RateLimiter.create(limit);
                        }
                    });

    public TradeResource(AppConfiguration config, TradeDao dao) {
        this.dao = dao;
        if (rateLimit == 0 || TOO_MANY_REQUESTS == null) {
            rateLimit = config.getRateLimit();
            TOO_MANY_REQUESTS = Response.status(429).entity("Too many requests per second for this user, "
                            + rateLimit + " are allowed").build();
        }
    }

    /**
     * We could do something more interesting with this...
     * @return hello.
     */
    @GET
    public Response hello() {
        RateLimiter rl = getRateLimiter(0);
        if (rl.tryAcquire(MAX_WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
            return Response.status(200).entity("hello").build();
        } else {
            return TOO_MANY_REQUESTS;
        }
    }

    @UnitOfWork
    @POST
    @Path("/trade")
    public Response submitMessage(TradeMessage tradeMessage) {
        RateLimiter rl = getRateLimiter(tradeMessage.getUserId());
        if (rl.tryAcquire(100, TimeUnit.MILLISECONDS)) {
            dao.insert(tradeMessage);
            LOG.debug("User: {} registered a trade", tradeMessage.getUserId());
            return Response.status(201).build();
        } else {
            LOG.info("User: {} exceeded their request rate", tradeMessage.getUserId());
            return TOO_MANY_REQUESTS;
        }
    }

    @UnitOfWork(readOnly = true)
    @Path("/trade")
    @GET
    public Response getAllUsersMessages(int userId) {
        RateLimiter rl = getRateLimiter(userId);
        if (rl.tryAcquire(MAX_WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
            LOG.debug("User: {} requested their trades", userId);
            return Response.ok().entity(dao.retrieveAll(userId)).build();
        } else {
            LOG.info("User: {} exceeded their request rate", userId);
            return TOO_MANY_REQUESTS;
        }
    }

    private RateLimiter getRateLimiter(Integer userId) {
        try {
            return rateLimitCache.get(userId);
        } catch (ExecutionException e) {
            LOG.error("Couldn't get a rate limiter for user: {}", userId, e);
            throw new RuntimeException(e);
        }
    }
}
