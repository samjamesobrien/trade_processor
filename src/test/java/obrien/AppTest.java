package obrien;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import obrien.configuration.AppConfiguration;

import java.io.IOException;

public class AppTest extends Application<AppConfiguration> {

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment)
            throws ClassNotFoundException, IOException {
    }

}
