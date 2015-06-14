create table tradeMessages (
	id serial,
	userId int not null,
	currencyFrom varchar(3) not null,
	currencyTo varchar(3) not null,
	amountSell numeric(15,6) not null,
	amountBuy numeric(15,6) not null,
	rate numeric(15,6) not null,
	timePlaced date not null,
	originatingCountry varchar(2) not null
);