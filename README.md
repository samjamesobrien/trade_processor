[![Build Status](https://travis-ci.org/samjamesobrien/trade_processor.svg)](https://travis-ci.org/samjamesobrien/trade_processor)

# trade_processor
toy project using dropwizard designed to be deployable to Heroku


#### Things I didn't have time to do

- Proper test coverage
- security and authentication
- GC of stored trades after a certain time and persisting overall metrics only
- more interesting metrics and processing
- more Interfaces, refactoring and more providers with configurable fields


Sample Query using curl
-----------------------

Jackson validates these and will reject malformed objects

Submit a trade

`curl -i -X POST {location}/api/trade  -H "Content-Type: application/json" -d '{"userId":"134256","currencyFrom":"EUR","currencyTo":"GBP","amountSell":1000,"amountBuy":747.10,"rate":0.7471,"timePlaced":"24-JAN-15 10:27:44","originatingCountry":"FR"}'`

Get a users trades

`curl -i -X GET {location}/api/trade/134256  -H "Content-Type: application/json"`

The index page

`curl -i -X GET {location}/`


Currency values
---------------

Currency and exchange rates are stored to 6 decimal places, but exposed with 4.


Heroku configuration
--------------------

Heroku provides some environment variables for the port you need to bind to, and the database url including credentials.

In the Procfile we honor these environment variables, overriding fields set in the .yml.

Interestingly we cannot run arbitrary maven commands on Heroku, so everything that needs to be done such as db migration
or configuring classes from environment variables is run automatically by the component itself.

For the database, we have a modified getter which parses the Heroku environment variable if present and overrides our configuration.


Rate limiting
-------------

Configurable per user rate limiting has been implemented using guavas RateLimiter class.
Per user RateLimiter objects are cached.


Dependency Injection
--------------------

...was not used. In my experience using the Guice dropwizard plugin or dependency injection in general with dropwizard leads
to more trouble than it saves. The Dropwizard lifecycle means that in certain circumstances you will not have access to
configuration fields, as the injected Config class may not have been initialized, as it only exists after the bootstrap phase.

This breaks the abstraction that we can just magically have a config object when needed, and leads to some awkward code.

So for this project I opted against dependency injection although it would have worked just fine for this use case.


Flyway & database migration
---------------------------

Upon succesfully starting up and connecting to the database, the component will automatically perform the database migration, creating tables etc.

You can also run:

    `mvn clean compile flyway:migrate`


Run the component
-----------------

###### Note - for non Heroku usage, set the database credentials in your .yml as per the flyway config in the pom.xml

    `java -jar target/trade_processor-1.0-SNAPSHOT.jar server config.yml`


Clear the h2 db
---------------

For local development without needing postgres, h2 is used, it creates .db files in lieu of a proper database.

    `rm *.db`
