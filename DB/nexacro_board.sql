CREATE DATABASE nexacro_board DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE USER 'nexa'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON nexacro_board.* TO 'nexa'@'localhost';

CREATE TABLE MEMBER (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    pw VARCHAR(255),
    email VARCHAR(255),
    is_admin BOOLEAN,
    join_date TIMESTAMP
);

INSERT INTO MEMBER (id, name, pw, email, is_admin, join_date) VALUES ('test02', '테스트02', '1234', 'test01@test.com', FALSE, now());
INSERT INTO MEMBER (id, name, pw, email, is_admin, join_date) VALUES ('test01', '테스트01', '1234', 'test01@test.com', FALSE, now());
INSERT INTO MEMBER (id, name, pw, email, is_admin, join_date) VALUES ('admin', '관리자', 'adminpw', 'admin@admin.com', TRUE, now());

select * from member;