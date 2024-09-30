CREATE SCHEMA IF NOT EXISTS shop AUTHORIZATION robert;

CREATE TABLE IF NOT EXISTS shop.PRODUCTS
(
    id          serial PRIMARY KEY,
    SKU_CODE    VARCHAR(100),
    name        varchar(100),
    description varchar(300),
    price       double precision
);
