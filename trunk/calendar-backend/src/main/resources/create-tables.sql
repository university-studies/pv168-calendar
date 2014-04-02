CREATE TABLE EVENT (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    id_user BIGINT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(800),
    location VARCHAR(500),
    startDate TIMESTAMP,
    endDate TIMESTAMP,
    repeat INTEGER,
    repeatTimes INTEGER);

CREATE TABLE USERS (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30) NOT NULL,
    password VARCHAR(50),
    email VARCHAR(50));

CREATE TABLE REMINDER (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    id_event BIGINT REFERENCES EVENT,
    start TIMESTAMP);
