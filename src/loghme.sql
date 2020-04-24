create database Loghme;
use Loghme;

CREATE TABLE Restaurant (
	id                  char(63) PRIMARY KEY,
	name                TEXT NOT NULL,
	location_x          double NOT NULL,
	location_y          double NOT NULL,
	logo             	TEXT NOT NULL
);

TRUNCATE TABLE Restaurant;

CREATE TABLE Menu (
	restaurantId        char(63),
	foodName            varchar(255) NOT NULL,
	description         TEXT NOT NULL,
	popularity          double NOT NULL,
	price             	int NOT NULL,
    image				TEXT NOT NULL,
    primary key (restaurantId, foodName)
);

drop table Menu;

