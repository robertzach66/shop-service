CREATE SCHEMA IF NOT EXISTS shop AUTHORIZATION robert;

CREATE TABLE IF NOT EXISTS shop.PRODUCTS
(
    id          serial PRIMARY KEY,
    SKU_CODE    VARCHAR(100) NOT NULL CONSTRAINT ux_sku_code UNIQUE,
    name        varchar(100) NOT NULL,
    description varchar(300),
    price       double precision NOT NULL
);
