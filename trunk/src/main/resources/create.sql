CREATE TABLE "EVENT" (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "title" VARCHAR(200) NOT NULL,
    "description" VARCHAR(800),
    "location" VARCHAR(500)
);


connect 'jdbc:derby://localhost:1527/calendar;user=user;password=user;create=true';
CREATE TABLE EVENT (id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, title VARCHAR(200) NOT NULL, description VARCHAR(800),location VARCHAR(500));
insert into event (title,description,location) values('aaa','desc','trstena');

CREATE TABLE USERS (id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, login VARCHAR(30) NOT NULL, hashedPassword VARCHAR(50), email VARCHAR(50));
