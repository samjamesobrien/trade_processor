package obrien.Util;

/**
 * Some common default values, saves us from defining them everywhere.
 */
public final class DefaultValues {
    public static final int DEFAULT_CACHE_SIZE = 10000;
    public static final int DEFAULT_RATE_LIMIT_SECONDS = 100;
    public static final int LAST_N_TRADES = 50;

    // Avoid blocking the calling thread, you either exceeded your rate limit or you didn't
    public static final int MAX_WAIT_MILLIS = 0;

    /**
     * You cannot instantiate this class.
     */
    private DefaultValues() {
    }
}
