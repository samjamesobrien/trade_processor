[![Build Status](https://travis-ci.org/samjamesobrien/trade_processor.svg)](https://travis-ci.org/samjamesobrien/trade_processor)

# trade_processor
toy project using dropwizard designed to be deployable to Heroku


Sample Query using curl
-----------------------

Jackson validates these and will reject malformed objects

`curl -i -X POST localhost:9918/trade  -H "Content-Type: application/json" -d '{"userId":"134256","currencyFrom":"EUR","currencyTo":"GBP","amountSell":1000,"amountBuy":747.10,"rate":0.7471,"timePlaced":"24-JAN-15 10:27:44","originatingCountry":"FR"}'`

`curl -i -X GET localhost:9918/trade  -H "Content-Type: application/json" -d '134256'`


Heroku configuration
--------------------

Heroku provides some environment variables for the port you need to bind to, and the database url including credentials.

In the Procfile we honor these environment variables, overriding fields set in the .yml.

For the database, we have a modified getter which parses the Heroku environment variable if present and overrides our configuration.


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

rm *.db
