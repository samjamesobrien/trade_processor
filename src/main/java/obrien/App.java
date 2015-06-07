package obrien;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.joschi.dropwizard.flyway.FlywayBundle;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import obrien.configuration.AppConfiguration;
import obrien.entity.TradeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class.
 */
public class App extends Application<AppConfiguration> {

    private final HibernateBundle<AppConfiguration> hibernateBundle;
    private final FlywayBundle flywayBundle;

    private static final Logger log = LoggerFactory.getLogger(App.class);

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

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(this.hibernateBundle);
        bootstrap.addBundle(this.flywayBundle);
        bootstrap.getObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(TradeResource.class);
    }
}
