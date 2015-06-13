package obrien;

import com.github.rjeschke.txtmark.Processor;
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
import java.io.File;
import java.io.IOException;
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

    // Avoid blocking the calling thread, you either exceeded your rate limit or you didn't
    private static final int MAX_WAIT_MILLIS = 0;

    private static int rateLimit;
    private static String TOO_MANY_REQUESTS;
    private static String index;

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
            TOO_MANY_REQUESTS = "Too many requests per second for this user, " + rateLimit + " are allowed";
        }
    }

    /**
     * Return the README.md file as html.
     * @return index page generated from .md file.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response index() {
        RateLimiter rl = getRateLimiter(0);

        if (index == null) {
            File file = new File("README.md");
            try {
                index = Processor.process(file);
            } catch (IOException e) {
                index = "default index";
                LOG.error("Couldn't read file");
            }
        }

        if (rl.tryAcquire(MAX_WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
            return Response.status(200).entity(index).build();
        } else {
            LOG.info("Index exceeded request rate");
            return Response.status(429).entity(TOO_MANY_REQUESTS).build();
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
            return Response.status(429).entity(TOO_MANY_REQUESTS).build();
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
            return Response.status(429).entity(TOO_MANY_REQUESTS).build();
        }
    }

    private static RateLimiter getRateLimiter(Integer userId) {
        try {
            return rateLimitCache.get(userId);
        } catch (ExecutionException e) {
            LOG.error("Couldn't get a rate limiter for user: {}", userId, e);
            throw new RuntimeException(e);
        }
    }
}
