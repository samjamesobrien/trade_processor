package obrien.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Top-level configuration for the component.
 */
public class AppConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();
    @NotNull
    private int rateLimit;

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public int getRateLimit(){
        return rateLimit;
    }

}
