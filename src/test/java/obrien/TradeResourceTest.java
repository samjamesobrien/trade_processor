package obrien;

import obrien.configuration.AppConfiguration;
import obrien.dao.TradeDao;
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

    @Before
    public void setup() {
        AppConfiguration config = mock(AppConfiguration.class);
        stub(config.getRateLimit()).toReturn(1);
        tradeResource = new TradeResource(config, mock(TradeDao.class));
    }

    @Test
    public void testRateLimiting() {
        boolean limitHit = false;
        for (int i = 0; i < 1000; i++) {
            Response r = tradeResource.hello();
            if (r.getStatus() != 200) {
                limitHit = true;
                break;
            }
        }
        assertTrue(limitHit);
    }

}
