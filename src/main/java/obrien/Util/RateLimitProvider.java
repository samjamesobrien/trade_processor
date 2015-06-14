package obrien.Util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import obrien.configuration.AppConfiguration;
import obrien.resources.TradeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Provides rate limiters.
 * <p>Mostly used to keep a common cache of rate limiters per some common field,
 * eg. users each have a rate limit.</p>
 */
public class RateLimitProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TradeResource.class);
    private static final int CACHE_SIZE = 10000;
    private static final int DEFAULT_RATE_LIMIT_SECONDS = 10;

    private static int rateLimit;

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

    public RateLimitProvider(AppConfiguration config) {
        if (rateLimit == 0) {
            rateLimit = config.getRateLimit();
        }
    }


    /**
     * Get a new or cached RateLimiter for the user if it exists.
     * @param userId the key for the cached rate limiter.
     * @return this users rate limiter.
     */
    public RateLimiter getRateLimiter(Integer userId) {
        try {
            return rateLimitCache.get(userId);
        } catch (ExecutionException e) {
            LOG.error("Couldn't get a rate limiter for user: {}", userId, e);
            throw new RuntimeException(e);
        }
    }
}
