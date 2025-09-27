CREATE TABLE Users(
Id INTEGER PRIMARY KEY AUTOINCREMENT,
Name TEXT,
LastName TEXT,
isAdmin INTEGER,
Password TEXT,
userName TEXT
)


CREATE TABLE Status (
timeStamp datetime PRIMARY KEY,
längenGrad REAl,
breitenGrad REAL,
battery INTEGER,
UserId INTEGER,
FOREIGN KEY(UserId) REFERENCES Users(Id)
)
