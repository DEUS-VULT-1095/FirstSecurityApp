CREATE TABLE Person(
                       id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                       username VARCHAR (100) NOT NULL,
                       year_of_birth INT NOT NULL,
                       "password" VARCHAR NOT NULL
);