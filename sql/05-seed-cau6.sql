-- Cau 6: Seed bo sung sach de dat tong so 21 cuon
-- Phuc vu kiem thu phan trang Admin 10 sach / trang -> 10 / 10 / 1
-- An toan, co the chay lai (IF NOT EXISTS), khong sua cot goc, khong tao lai DB.
SET XACT_ABORT ON;
BEGIN TRANSACTION;

DECLARE @aIdA int = (SELECT MIN(author_id) FROM dbo.author WHERE author_name = 'An Nguyen');
DECLARE @aIdB int = (SELECT MIN(author_id) FROM dbo.author WHERE author_name = 'Minh Tran');

-- 10 sach bo sung (ISBN 241330212 den 241330221)
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330212)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330212, 'Du Khach Ky La', 'NXB Kim Dong', 88.00, 'Cau chuyen phieu luu ky bi cua nhung nguoi lu hanh.', '2025-01-10', 'assets/images/book-placeholder.svg', 20);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330213)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330213, 'Ngon Den Dem Dong', 'NXB Tre', 115.00, 'Tam su am ap trong nhung dem dong gia lanh.', '2025-01-15', 'assets/images/book-placeholder.svg', 15);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330214)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330214, 'Tieng Vong Rung Sau', 'NXB Hoi Nha Van', 150.00, 'Kham pha he sinh thai va ve dep rung nhiet doi.', '2025-01-20', 'assets/images/book-placeholder.svg', 18);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330215)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330215, 'Mat Troi Moc O Phia Tay', 'NXB Tre', 130.00, 'Goc nhin khoa hoc vien tuong day kich tinh.', '2025-02-01', 'assets/images/book-placeholder.svg', 22);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330216)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330216, 'Chiec Dong Ho Cat', 'NXB Kim Dong', 99.00, 'Triet ly ve thoi gian va nhung co hoi trong cuoc song.', '2025-02-10', 'assets/images/book-placeholder.svg', 30);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330217)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330217, 'Con Duong Menh Mong', 'NXB Hoi Nha Van', 165.00, 'Nhung trai nghiem tren cac neo duong dat nuoc.', '2025-02-18', 'assets/images/book-placeholder.svg', 12);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330218)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330218, 'Nhung Vi Tinh Tu', 'NXB Tong Hop', 142.00, 'Cam hung tu bau troi sao va uoc mo tuoi tre.', '2025-03-01', 'assets/images/book-placeholder.svg', 25);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330219)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330219, 'Tieng Rao Dem', 'NXB Tre', 85.00, 'Lat cat cuoc song do thi ve dem day cam xuc.', '2025-03-05', 'assets/images/book-placeholder.svg', 40);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330220)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330220, 'Ban Tinh Ca Mua Ha', 'NXB Kim Dong', 105.00, 'Ky niem tuoi hoc tro duoi nhung hang phuong vi.', '2025-03-10', 'assets/images/book-placeholder.svg', 16);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330221)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330221, 'Giac Mo Xanh', 'NXB Tri Thuc', 180.00, 'Khat vong bao ve moi truong va phat trien ben vung.', '2025-03-15', 'assets/images/book-placeholder.svg', 14);

-- Gan tac gia cho cac sach moi
INSERT dbo.book_author (bookid, author_id)
SELECT b.bookid, @aIdA
FROM dbo.books b
WHERE b.isbn IN (241330212, 241330213, 241330216, 241330218, 241330220)
  AND NOT EXISTS (SELECT 1 FROM dbo.book_author ba WHERE ba.bookid = b.bookid AND ba.author_id = @aIdA);

INSERT dbo.book_author (bookid, author_id)
SELECT b.bookid, @aIdB
FROM dbo.books b
WHERE b.isbn IN (241330214, 241330215, 241330217, 241330219, 241330221)
  AND NOT EXISTS (SELECT 1 FROM dbo.book_author ba WHERE ba.bookid = b.bookid AND ba.author_id = @aIdB);

COMMIT TRANSACTION;

