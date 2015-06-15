package obrien;

import obrien.Util.RateLimitProvider;
import obrien.configuration.AppConfiguration;
import obrien.dao.TradeDao;
import obrien.entity.TradeMessage;
import obrien.resources.TradeResource;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

/**
 * Test the trade resource.
 */
public class TradeResourceTest {

    private TradeResource tradeResource;
    TradeMessage tradeMessage;

    @Before
    public void setup() {
        AppConfiguration config = mock(AppConfiguration.class);
        TradeDao dao = mock(TradeDao.class);
        RateLimitProvider rlp = new RateLimitProvider(config);

        stub(config.getRateLimit()).toReturn(3);
        tradeResource = new TradeResource(config, dao, rlp);

        tradeMessage = new TradeMessage();
        tradeMessage.setId(12345);
    }

    /**
     * For a rate limit of 5 per second, call submit message 50 times in quick succession.
     * <p>Could produce a race condition in some circumstances but I think it is unlikely.
     * This is probably a deterministic test.</p>
     */
    @Test
    public void testRateLimiting() {
        boolean limitHit = false;
        for (int i = 0; i < 50; i++) {
            Response r = tradeResource.submitMessage(tradeMessage);
            if (r.getStatus() != 200) {
                limitHit = true;
                break;
            }
        }
        assertTrue(limitHit);
    }
}
