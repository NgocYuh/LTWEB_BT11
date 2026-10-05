-- Re-runnable sample catalog; existing records are not overwritten.
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF NOT EXISTS (SELECT 1 FROM dbo.author WHERE author_name = 'An Nguyen')
    INSERT dbo.author (author_name, date_of_birth) VALUES ('An Nguyen', '1985-04-12');
IF NOT EXISTS (SELECT 1 FROM dbo.author WHERE author_name = 'Minh Tran')
    INSERT dbo.author (author_name, date_of_birth) VALUES ('Minh Tran', '1990-09-23');

DECLARE @authorA int = (SELECT MIN(author_id) FROM dbo.author WHERE author_name = 'An Nguyen');
DECLARE @authorB int = (SELECT MIN(author_id) FROM dbo.author WHERE author_name = 'Minh Tran');
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330201)
    INSERT dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (241330201,'The Quiet Library','HNH Press',125.00,'A journey through books and the people who read them.','2024-03-01','assets/images/book-placeholder.svg',20);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330208)
    INSERT dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (241330208,'Letters from Tomorrow','HNH Press',155.00,'Stories about curiosity and new beginnings.','2025-04-15','assets/images/book-placeholder.svg',15);

INSERT dbo.book_author (bookid, author_id)
SELECT b.bookid, CASE WHEN b.isbn = 241330201 THEN @authorA ELSE @authorB END
FROM dbo.books b
WHERE b.isbn IN (241330201,241330208)
AND NOT EXISTS (SELECT 1 FROM dbo.book_author ba WHERE ba.bookid=b.bookid
    AND ba.author_id=CASE WHEN b.isbn=241330201 THEN @authorA ELSE @authorB END);
COMMIT TRANSACTION;
