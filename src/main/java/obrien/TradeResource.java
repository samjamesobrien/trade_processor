package obrien;

import obrien.configuration.AppConfiguration;
import obrien.dao.Dao;
import obrien.dao.TradeDao;
import obrien.entity.TradeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Trade API resource.
 */
@Path("/trade")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TradeResource {

    private static final Logger log = LoggerFactory.getLogger(TradeResource.class);

    private final Dao<TradeMessage> dao;
    private final int rateLimit;

    public TradeResource(AppConfiguration config, TradeDao dao) {
        this.dao = dao;
        this.rateLimit = config.getRateLimit();
    }

    @POST
    public Response submitMessage(TradeMessage tradeMessage) {
        // todo - hystrix rate limiting
        dao.insert(tradeMessage);
        return Response.ok().build();
    }

}
