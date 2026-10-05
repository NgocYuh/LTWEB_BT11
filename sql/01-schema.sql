-- Cau 1: run in an EMPTY HNHBOOKSTORE database. No DROP, no cascade.
-- The database connection/creation is configured separately.
SET XACT_ABORT ON;
BEGIN TRANSACTION;

CREATE TABLE dbo.books (
    bookid int IDENTITY(1,1) NOT NULL,
    isbn int NULL,
    title varchar(200) NULL,
    publisher varchar(100) NULL,
    price decimal(6,2) NULL,
    description text COLLATE Vietnamese_100_CI_AS NULL,
    publish_date date NULL,
    cover_image varchar(100) NULL,
    quantity int NULL,
    CONSTRAINT PK_books PRIMARY KEY (bookid)
);

CREATE TABLE dbo.users (
    id int IDENTITY(1,1) NOT NULL,
    email varchar(50) NOT NULL,
    fullname nvarchar(50) NULL,
    phone int NULL,
    passwd varchar(32) NOT NULL,
    signup_date datetime NULL,
    last_login datetime NULL,
    is_admin bit NULL,
    CONSTRAINT PK_users PRIMARY KEY (id)
);

CREATE TABLE dbo.author (
    author_id int IDENTITY(1,1) NOT NULL,
    author_name varchar(100) NULL,
    date_of_birth date NULL,
    CONSTRAINT PK_author PRIMARY KEY (author_id)
);

CREATE TABLE dbo.book_author (
    bookid int NOT NULL,
    author_id int NOT NULL,
    CONSTRAINT PK_book_author PRIMARY KEY (bookid, author_id),
    CONSTRAINT FK_book_author_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid),
    CONSTRAINT FK_book_author_author FOREIGN KEY (author_id) REFERENCES dbo.author(author_id)
);

CREATE TABLE dbo.rating (
    userid int NOT NULL,
    bookid int NOT NULL,
    rating tinyint NULL,
    review_text text COLLATE Vietnamese_100_CI_AS NULL,
    CONSTRAINT PK_rating PRIMARY KEY (userid, bookid),
    CONSTRAINT FK_rating_users FOREIGN KEY (userid) REFERENCES dbo.users(id),
    CONSTRAINT FK_rating_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid)
);

COMMIT TRANSACTION;
