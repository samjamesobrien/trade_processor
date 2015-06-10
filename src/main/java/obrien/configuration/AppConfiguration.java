package obrien.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Top-level configuration for the component.
 */
public class AppConfiguration extends Configuration {
    private static final Logger log = LoggerFactory.getLogger(AppConfiguration.class);

    private static final String DATABASE_URL = "DATABASE_URL";

    private static boolean herokuCredentialsSet = false;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();
    @NotNull
    private int rateLimit;

    public DataSourceFactory getDataSourceFactory() {
        return setHerokuDBCredentials(database);
    }

    public int getRateLimit(){
        return rateLimit;
    }

    /**
     * Override the database credentials with the ones provided by Heroku.
     * <p>Heroku provides our credentials in an environment variable, this takes advantage of that.</p>
     * @param db the database config to override
     * @return
     */
    private static DataSourceFactory setHerokuDBCredentials(DataSourceFactory db) {
        if (!herokuCredentialsSet && System.getenv().containsKey(DATABASE_URL)) {
            URI uri;
            try {
                uri = new URI(System.getenv(DATABASE_URL));
            } catch (URISyntaxException e) {
                log.error("Something was wrong with the credentials in the $"
                        + DATABASE_URL + " environment variable", e);
                herokuCredentialsSet = true;
                return db;
            }

            db.setUser(uri.getUserInfo().split(":")[0]);
            db.setPassword(uri.getUserInfo().split(":")[1]);
            db.setDriverClass(org.postgresql.Driver.class.getName());
            db.setUrl("jdbc:postgresql://" + uri.getHost() + ':' + uri.getPort() + uri.getPath()
                    + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
            herokuCredentialsSet = true;
        }
        return db;
    }
}
