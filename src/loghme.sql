create database Loghme;
use Loghme;

CREATE TABLE Restaurant (
                            id                  char(63) PRIMARY KEY,
                            name                TEXT NOT NULL,
                            location_x          double NOT NULL,
                            location_y          double NOT NULL,
                            logo             	TEXT NOT NULL
);



CREATE TABLE OrdinaryFood (
                              restaurantId        varchar(255),
                              foodName            varchar(255) NOT NULL,
                              description         TEXT NOT NULL,
                              popularity          double NOT NULL,
                              price             	int NOT NULL,
                              image				TEXT NOT NULL,
                              primary key (restaurantId, foodName)
);


CREATE TABLE PartyFood(
                          restaurantId        varchar(255),
                          foodName            varchar(255) NOT NULL,
                          description         TEXT NOT NULL,
                          popularity          double NOT NULL,
                          price             	int NOT NULL,
                          oldPrice			int NOT NULL,
                          count				int NOT NULL,
                          image				TEXT NOT NULL,
                          primary key (restaurantId, foodName)
);

CREATE TABLE User(
                     username        	varchar(63) primary key,
                     name            	varchar(63) NOT NULL,
                     lastName         	varchar(63) NOT NULL,
                     phoneNumber         varchar(63) unique NOT NULL,
                     emailAddress        varchar(255) unique not NULL,
                     credit				int NOT NULL,
                     password			varchar(63) NOT NULL
);



CREATE TABLE OrdinaryOrders(
                               username varchar(63),
                               foodName varchar(63) NOT NULL,
                               restaurantId varchar(63) NOT NULL,
                               foodCount int NOT NULL,
                               orderId int NOT NULL,
                               foodPrice int NOT NULL,
                               status varchar(63) NOT NULL,
                               primary key (username,orderId,foodName,restaurantId)
);



CREATE TABLE DiscountOrders(
                               username varchar(63),
                               foodName varchar(63) NOT NULL,
                               restaurantId varchar(63) NOT NULL,
                               foodCount int NOT NULL,
                               orderId int NOT NULL,
                               foodPrice int NOT NULL,
                               status varchar(63) NOT NULL,
                               primary key (username,orderId,foodName,restaurantId)
);








