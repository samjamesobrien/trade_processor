package obrien.resources;

import com.github.rjeschke.txtmark.Processor;
import com.google.common.util.concurrent.RateLimiter;
import obrien.configuration.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static obrien.Util.DefaultValues.MAX_WAIT_MILLIS;

/**
 * Just returns the README.md parsed into a html page.
 */
@Path("/readme")
public class ReadmeResource {
    private static final Logger LOG = LoggerFactory.getLogger(TradeResource.class);

    private static int rateLimit;
    private static String TOO_MANY_REQUESTS;
    private static String readme;
    private static RateLimiter rateLimiter;

    public ReadmeResource(AppConfiguration config) {
        if (rateLimit == 0 || TOO_MANY_REQUESTS == null) {
            rateLimit = config.getRateLimit();
            TOO_MANY_REQUESTS = "Too many requests per second for this user, " + rateLimit + " are allowed";
        }
        rateLimiter = RateLimiter.create(rateLimit);
    }

    /**
     * Return the README.md file as html.
     * @return readme.html page generated from .md file.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response readme() {
        if (readme == null) {
            File file = new File("README.md");
            try {
                // Convert .md to .html
                readme = Processor.process(file);
            } catch (IOException e) {
                LOG.error("Couldn't read file from path: {}", file.getPath());
            }
        }

        if (rateLimiter.tryAcquire(MAX_WAIT_MILLIS, TimeUnit.MILLISECONDS) && readme != null) {
            return Response.status(200).entity(readme).build();
        } else if (readme == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            LOG.info("readme page request rate exceeded, limit: {}", rateLimit);
            return Response.status(429).entity(TOO_MANY_REQUESTS).build();
        }
    }
}
