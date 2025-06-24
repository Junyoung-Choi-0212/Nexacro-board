CREATE TABLE MEMBER (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    pw VARCHAR(255),
    email VARCHAR(255),
    is_admin BOOLEAN,
    join_date TIMESTAMP
);