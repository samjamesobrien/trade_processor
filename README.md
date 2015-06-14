[![Build Status](https://travis-ci.org/samjamesobrien/trade_processor.svg)](https://travis-ci.org/samjamesobrien/trade_processor)

# trade_processor
toy project using dropwizard designed to be deployable to Heroku

## TODO
This is work in progress, remaining to do is some sort of GC method to transform consumed trades into raw metrics after
a certain time in the DB and to clear out space unless I pay for a bigger DB. At a minimum a dropwizard task which
clears the DB is required.

I'd like to feed the trades into an RX java observable stream for processing and extract metrics, requests for trades
should also be cached, we should only read from the DB at start-up or after the cache expires, the cache can update in
parallel with persisting objects, I'd implement a CachedDAO interface for this.


Sample Query using curl
-----------------------

Jackson validates these and will reject malformed objects

`curl -i -X POST {location}/trade  -H "Content-Type: application/json" -d '{"userId":"134256","currencyFrom":"EUR","currencyTo":"GBP","amountSell":1000,"amountBuy":747.10,"rate":0.7471,"timePlaced":"24-JAN-15 10:27:44","originatingCountry":"FR"}'`

`curl -i -X GET {location}/trade  -H "Content-Type: application/json" -d '134256'`

`curl -i -X GET {location}/`


Floating point errors
---------------------

Unfortunately the specified json values for trades are floating point numbers, so even though we store them as BigDecimal,
floating point errors are present on the values at deserialization time.

I think it would be a questionable tactic to perform rounding at ingress time but is easily achievable,
but represents a code smell. The message API spec should change, but the above is the spec I received and so we work with that.


Heroku configuration
--------------------

Heroku provides some environment variables for the port you need to bind to, and the database url including credentials.

In the Procfile we honor these environment variables, overriding fields set in the .yml.

Interestingly we cannot run arbitrary maven commands on Heroku, so everything that needs to be done such as db migration
 or configuring classes from environment variables is run automatically by the component itself.

For the database, we have a modified getter which parses the Heroku environment variable if present and overrides our configuration.


Rate limiting
-------------

Configurable per user rate limiting has been implemented using guavas RateLimiter class. I originally had a method which
wrapped a lambda with a rate limit, such as persisting or retrieving objects to/from the db,
but for our small number of resource methods it didn't reduce the lines of code and reduced legibility.

Per user RateLimiter objects are cached.


Dependency Injection
--------------------

...was not used. In my experience using the Guice dropwizard plugin or dependency injection in general with dropwizard leads
to more trouble than it saves. The Dropwizard lifecycle means that in certain circumstances you will not have access to
configuration fields, as the injected Config class may not have been initialized, as it only exists after the bootstrap phase.

This breaks the abstraction that we can just magically have a config object when needed, and leads to awkward code.

So for this project I opted against dependency injection.


Flyway & database migration
---------------------------

Upon succesfully starting up and connecting to the database, the component will automatically perform the database migration, creating tables etc.

You can also run:

    `mvn clean compile flyway:migrate`


Run the component
-----------------

###### Note - for non Heroku usage, set the databse credentials in your .yml as per the flyway config in the pom.xml

    `java -jar target/trade_processor-1.0-SNAPSHOT.jar server some_config.yml`


Clear the h2 db
---------------

For local development without needing postgres, h2 is used, it creates .db files in lieu of a proper database.

    `rm *.db`
