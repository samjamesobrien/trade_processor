package obrien;

import com.github.joschi.dropwizard.flyway.FlywayBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import obrien.Util.RateLimitProvider;
import obrien.configuration.AppConfiguration;
import obrien.dao.TradeDao;
import obrien.entity.TradeMessage;
import obrien.resources.ReadmeResource;
import obrien.resources.TradeResource;
import obrien.resources.TrendsResource;
import org.flywaydb.core.Flyway;

import java.text.SimpleDateFormat;

/**
 * Main class.
 */
public class App extends Application<AppConfiguration> {

    private final HibernateBundle<AppConfiguration> hibernateBundle;
    private final FlywayBundle flywayBundle;


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    public App () {
        this.hibernateBundle = new HibernateBundle<AppConfiguration>(TradeMessage.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        };
        this.flywayBundle = new FlywayBundle<AppConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        };
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(this.hibernateBundle);
        bootstrap.addBundle(this.flywayBundle);
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
        bootstrap.getObjectMapper().setDateFormat(dateFormat);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        Flyway flyway = new Flyway();
        DataSourceFactory db = configuration.getDataSourceFactory();

        flyway.setDataSource(db.getUrl(), db.getUser(), db.getPassword());
        flyway.migrate();

        // Get our dependency instances
        final TradeDao tradeDao = new TradeDao(hibernateBundle.getSessionFactory());
        final RateLimitProvider rateLimitProvider = new RateLimitProvider(configuration);

        // instantiate and register our resources
        final ReadmeResource readmeResource = new ReadmeResource(configuration);
        final TradeResource tradeResource = new TradeResource(configuration, tradeDao, rateLimitProvider);
        final TrendsResource trendsResource = new TrendsResource();

        environment.jersey().register(readmeResource);
        environment.jersey().register(tradeResource);
        environment.jersey().register(trendsResource);
    }
}
