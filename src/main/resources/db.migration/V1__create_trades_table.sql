create table searches (
	id serial,
	userId int not null,
	currencyFrom varchar(3) not null,
	currencyTo varchar(3) not null,
	amountSell int not null,
	amountBuy int not null,
	rate float not null,
	timePlaced date not null,
	locale varchar(2) not null
);