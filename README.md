# trade_processor
toy project using dropwizard

Sample Query using curl
-----------------------

curl -i -X POST localhost:9918/trade  -H "Content-Type: application/json" -d '{"userId":"134256","currencyFrom":"EUR","currencyTo":"GBP","amountSell":1000,"amountBuy":747.10,"rate":0.7471,"timePlaced":"24-JAN-15 10:27:44","originatingCountry":"FR"}'

