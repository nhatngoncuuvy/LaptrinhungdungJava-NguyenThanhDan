CREATE DATABASE jdbc_db;
USE jdbc_db;

CREATE TABLE users (
    id INT PRIMARY KEY auto_increment,
    name VARCHAR(50) NOT NULL,
    age INT,
    address VARCHAR(50) NOT NULL,
    email VARCHAR(100)
);

INSERT INTO users (name, age, address, email) VALUES ('Hoang Ngoc Thuan', 20, 'BEN TRE', 'thuan@gmail.com');
INSERT INTO users (name, age, address, email) VALUES ('Nguyen Thanh Dan', 20, 'CA MAU', 'dan@gmail.com');
INSERT INTO users (name, age, address, email) VALUES ('Pham Duong Quang Thinh', 20, 'HO CHI MINH', 'thinh@gmail.com');
INSERT INTO users (name, age, address, email) VALUES ('Phung Xuan Chien', 20, 'THANH HOA', 'chien@gmail.com');

SELECT * FROM users;



