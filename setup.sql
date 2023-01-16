CREATE DATABASE shop;
\c shop
CREATE TABLE sales_history ( id SERIAL PRIMARY KEY, datetime TIMESTAMP, sales NUMERIC, points NUMERIC);
INSERT INTO sales_history (datetime, sales, points) VALUES ('2022-10-02T10:10:00Z',102.00,5);
INSERT INTO sales_history (datetime, sales, points) VALUES ('2022-10-02T10:00:00Z',1002.00,50);
INSERT INTO sales_history (datetime, sales, points) VALUES ('2022-10-02T10:15:00Z',3.00,15);
INSERT INTO sales_history (datetime, sales, points) VALUES ('2022-10-02T10:20:00Z',200.00,30);
INSERT INTO sales_history (datetime, sales, points) VALUES ('2022-10-02T13:15:00Z',1.00, 30);
INSERT INTO sales_history (datetime, sales, points) VALUES ('2022-10-03T10:25:00Z',500.00,50);
