CREATE TABLE PRODUCTS
(
    id          serial PRIMARY KEY,
    name        varchar(100),
    description varchar(300),
    price       double precision
);
--CREATE SEQUENCE PRODUCTS_SEQ;