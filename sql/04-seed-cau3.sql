-- Cau 3: Seed du lieu mau cho tac gia, sach va danh gia (reviews)
-- An toan, co the chay lai (IF NOT EXISTS), khong sua cot goc, khong tao lai DB.
SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- 1. Dam bao ton tai 2 tac gia An Nguyen va Minh Tran
IF NOT EXISTS (SELECT 1 FROM dbo.author WHERE author_name = 'An Nguyen')
    INSERT dbo.author (author_name, date_of_birth) VALUES ('An Nguyen', '1985-04-12');
IF NOT EXISTS (SELECT 1 FROM dbo.author WHERE author_name = 'Minh Tran')
    INSERT dbo.author (author_name, date_of_birth) VALUES ('Minh Tran', '1990-09-23');

DECLARE @aIdA int = (SELECT MIN(author_id) FROM dbo.author WHERE author_name = 'An Nguyen');
DECLARE @aIdB int = (SELECT MIN(author_id) FROM dbo.author WHERE author_name = 'Minh Tran');

-- 2. Bo sung sach cho An Nguyen (de dat toi thieu 7 sach: phan trang 3/3/1)
-- ISBN hop le trong pham vi int SQL Server (< 2.147.483.647)
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330202)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330202, 'Bong Thuyen Tren Song', 'NXB Tre', 135.00, 'Tieu thuyet ve ky uc va dong song que huong.', '2023-05-10', 'assets/images/book-placeholder.svg', 18);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330203)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330203, 'Mua Thu Sai Gon', 'NXB Kim Dong', 110.00, 'Nhung tan van nhe nhang ve Sai Gon nhung ngay sang thu.', '2023-09-18', 'assets/images/book-placeholder.svg', 25);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330204)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330204, 'Nhung Nguoi Giu Lua', 'NXB Hoi Nha Van', 145.00, 'Cau chuyen cam dong ve tinh thay tro qua nhieu the he.', '2024-01-15', 'assets/images/book-placeholder.svg', 12);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330205)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330205, 'Tieng Chim Truoc Binh Minh', 'NXB Tre', 95.00, 'Goc nhin moc mac ve cuoc song vung cao.', '2024-06-20', 'assets/images/book-placeholder.svg', 30);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330206)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330206, 'Hanh Trinh Qua Mien Ky Uc', 'NXB Tong Hop', 160.00, 'Hoi ky chan thuc ve nhung chuyen di doc chieu dai dat nuoc.', '2024-11-05', 'assets/images/book-placeholder.svg', 15);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330207)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330207, 'Nang Som Ben O Cua', 'NXB Kim Dong', 120.00, 'Tap truyen ngan danh cho tuoi tre day nhiet huyet.', '2025-02-14', 'assets/images/book-placeholder.svg', 22);

-- 3. Bo sung sach cho Minh Tran (de dat toi thieu 4 sach: phan trang 3/1)
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330209)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330209, 'Giai Dieu Thang Nam', 'NXB Tre', 140.00, 'Cau chuyen am nhac va khat vong vuon len.', '2024-05-12', 'assets/images/book-placeholder.svg', 16);

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330210)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330210, 'Buoc Chan Nguoi Mo Duong', 'NXB Tri Thuc', 175.00, 'Nghien cuu lich su va xa hoi doc dao.', '2024-10-30', 'assets/images/book-placeholder.svg', 10);

-- Sach chung ca 2 tac gia An Nguyen va Minh Tran de test quan he nhieu-nhieu
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 241330211)
    INSERT dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
    VALUES (241330211, 'Giao Lo Thoi Gian', 'NXB Hoi Nha Van', 190.00, 'Tac pham dong sang tac dac biet giua An Nguyen va Minh Tran.', '2025-01-01', 'assets/images/book-placeholder.svg', 14);

-- 4. Gan quan he book_author
-- Sach cua An Nguyen
INSERT dbo.book_author (bookid, author_id)
SELECT b.bookid, @aIdA
FROM dbo.books b
WHERE b.isbn IN (241330202, 241330203, 241330204, 241330205, 241330206, 241330207, 241330211)
AND NOT EXISTS (
    SELECT 1 FROM dbo.book_author ba WHERE ba.bookid = b.bookid AND ba.author_id = @aIdA
);

-- Sach cua Minh Tran
INSERT dbo.book_author (bookid, author_id)
SELECT b.bookid, @aIdB
FROM dbo.books b
WHERE b.isbn IN (241330209, 241330210, 241330211)
AND NOT EXISTS (
    SELECT 1 FROM dbo.book_author ba WHERE ba.bookid = b.bookid AND ba.author_id = @aIdB
);

-- 5. Seed danh gia (ratings/reviews) mau tu users da ton tai
DECLARE @uAdmin int = (SELECT MIN(id) FROM dbo.users WHERE email = 'admin@hnhbookstore.vn');
DECLARE @uUser int = (SELECT MIN(id) FROM dbo.users WHERE email = 'huyhoang.260806@gmail.com');

DECLARE @bQuiet int = (SELECT MIN(bookid) FROM dbo.books WHERE isbn = 241330201);
DECLARE @bBong int = (SELECT MIN(bookid) FROM dbo.books WHERE isbn = 241330202);
DECLARE @bMuaThu int = (SELECT MIN(bookid) FROM dbo.books WHERE isbn = 241330203);
DECLARE @bJoint int = (SELECT MIN(bookid) FROM dbo.books WHERE isbn = 241330211);

-- Sach 241330201 co 2 reviews
IF @uAdmin IS NOT NULL AND @bQuiet IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.rating WHERE userid = @uAdmin AND bookid = @bQuiet)
    INSERT dbo.rating (userid, bookid, rating, review_text) VALUES (@uAdmin, @bQuiet, 9, 'Mot cuon sach rat y nghia ve nhung nguoi yeu sach va doc sach.');

IF @uUser IS NOT NULL AND @bQuiet IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.rating WHERE userid = @uUser AND bookid = @bQuiet)
    INSERT dbo.rating (userid, bookid, rating, review_text) VALUES (@uUser, @bQuiet, 10, 'Van phong nhe nhang, loi cuon, doc rat suy ngam.');

-- Sach 241330202 co 1 review
IF @uUser IS NOT NULL AND @bBong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.rating WHERE userid = @uUser AND bookid = @bBong)
    INSERT dbo.rating (userid, bookid, rating, review_text) VALUES (@uUser, @bBong, 8, 'Hinh anh song nuoc mien Tay hien len that song dong.');

-- Sach 241330211 (chung tac gia) co 1 review tu Admin
IF @uAdmin IS NOT NULL AND @bJoint IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.rating WHERE userid = @uAdmin AND bookid = @bJoint)
    INSERT dbo.rating (userid, bookid, rating, review_text) VALUES (@uAdmin, @bJoint, 9, 'Su ket hop tuyet voi giua hai van phong doc dao.');

-- Cac sach khac (nhu 241330203, 241330204, ...) khong co review nao (Reviews (0))

COMMIT TRANSACTION;

