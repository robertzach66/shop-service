CREATE SCHEMA IF NOT EXISTS shop AUTHORIZATION robert;

CREATE TABLE IF NOT EXISTS shop.ORDERS
(
    id              serial PRIMARY KEY,
    ORDER_NUMBER    VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS shop.ORDER_ITEMS
(
    id          serial PRIMARY KEY,
    ORDER_ID    integer,
    SKU_CODE    VARCHAR(100),
    PRICE       double precision,
    quantity    INTEGER,
    CONSTRAINT fk_order FOREIGN KEY (ORDER_ID) REFERENCES shop.ORDERS (id)
);
