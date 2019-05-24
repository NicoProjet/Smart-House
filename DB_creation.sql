DROP DATABASE IF EXISTS printemps;
CREATE DATABASE printemps;

USE printemps;


DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
    Users_ID        INTEGER UNSIGNED UNIQUE AUTO_INCREMENT,
    Name            VARCHAR(50),
    IsAdmin         BOOLEAN,
    PRIMARY KEY (Users_ID)
);


DROP TABLE IF EXISTS Rooms;
CREATE TABLE Rooms (
    Rooms_ID        INTEGER UNSIGNED UNIQUE AUTO_INCREMENT,
    Name            VARCHAR(50),
    SecPerDegree    INTEGER UNSIGNED,
    Users_ID        INTEGER UNSIGNED,
    PRIMARY KEY (Rooms_ID),
    FOREIGN KEY (Users_ID) REFERENCES Users(Users_ID)
);
-- Foreign key Users_ID points to an owner of the room, column can be empty.


DROP TABLE IF EXISTS Dates;
CREATE TABLE Dates (
    Dates_ID        INTEGER UNSIGNED UNIQUE AUTO_INCREMENT,
    DataDate        DATE,
    PRIMARY KEY (Dates_ID)
);


DROP TABLE IF EXISTS DataTable;
CREATE TABLE DataTable (
    DataTable_ID    INTEGER UNSIGNED UNIQUE AUTO_INCREMENT,
    Users_ID        INTEGER UNSIGNED,
    Rooms_ID        INTEGER UNSIGNED,
    Dates_ID        INTEGER UNSIGNED,
    TimeOfChange    INTEGER UNSIGNED,
    DesiredDegree   INTEGER SIGNED,
    PRIMARY KEY (DataTable_ID),
    FOREIGN KEY (Users_ID) REFERENCES Users(Users_ID),
    FOREIGN KEY (Rooms_ID) REFERENCES Rooms(Rooms_ID),
    FOREIGN KEY (Dates_ID) REFERENCES Dates(Dates_ID)
);
