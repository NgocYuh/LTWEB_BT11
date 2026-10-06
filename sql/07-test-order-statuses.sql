-- Script cap nhat va kiem thu 8 trang thai don hang:
-- 1. Don hang moi (PENDING / NEW)
-- 2. Da xac nhan (CONFIRMED)
-- 3. Chuan bi hang (PREPARING)
-- 4. Van chuyen (SHIPPING)
-- 5. Giao hang (DELIVERING)
-- 6. Da giao (DELIVERED)
-- 7. Don hang huy (CANCELLED)
-- 8. Don hang hoan (RETURNED)

SET XACT_ABORT ON;
USE HNHBOOKSTORE;
GO

-- Cap nhat cac don hang hien co sang cac trang thai mau:
IF EXISTS (SELECT 1 FROM dbo.orders WHERE order_id = 1)
    UPDATE dbo.orders SET status = 'PENDING' WHERE order_id = 1;

IF EXISTS (SELECT 1 FROM dbo.orders WHERE order_id = 2)
    UPDATE dbo.orders SET status = 'CONFIRMED' WHERE order_id = 2;

IF EXISTS (SELECT 1 FROM dbo.orders WHERE order_id = 3)
    UPDATE dbo.orders SET status = 'PREPARING' WHERE order_id = 3;

IF EXISTS (SELECT 1 FROM dbo.orders WHERE order_id = 4)
    UPDATE dbo.orders SET status = 'SHIPPING' WHERE order_id = 4;

-- Tao them cac don hang mau cho cac trang thai con lai neu chua du:
IF NOT EXISTS (SELECT 1 FROM dbo.orders WHERE status = 'DELIVERING')
BEGIN
    INSERT INTO dbo.orders (user_id, customer_name, phone, shipping_address, note, total_amount, payment_method, status, created_at)
    VALUES (1, N'Nguyen Van Huy', '0987654321', N'So 1 Vo Van Ngan, TP Thu Duc', N'Giao hang gio hanh chinh', 250.00, 'COD', 'DELIVERING', DATEADD(MINUTE, -90, GETDATE()));
    
    DECLARE @new_id5 int = SCOPE_IDENTITY();
    INSERT INTO dbo.order_details (order_id, bookid, book_title, quantity, unit_price, subtotal)
    VALUES (@new_id5, 1, 'The Quiet Library', 2, 125.00, 250.00);
END;

IF NOT EXISTS (SELECT 1 FROM dbo.orders WHERE status = 'DELIVERED')
BEGIN
    INSERT INTO dbo.orders (user_id, customer_name, phone, shipping_address, note, total_amount, payment_method, status, created_at)
    VALUES (1, N'Nguyen Van Huy', '0987654321', N'So 1 Vo Van Ngan, TP Thu Duc', N'Da nhan hang thanh cong', 180.00, 'COD', 'DELIVERED', DATEADD(DAY, -1, GETDATE()));
    
    DECLARE @new_id6 int = SCOPE_IDENTITY();
    INSERT INTO dbo.order_details (order_id, bookid, book_title, quantity, unit_price, subtotal)
    VALUES (@new_id6, 2, 'Letters from Tomorrow', 1, 180.00, 180.00);
END;

IF NOT EXISTS (SELECT 1 FROM dbo.orders WHERE status = 'CANCELLED')
BEGIN
    INSERT INTO dbo.orders (user_id, customer_name, phone, shipping_address, note, total_amount, payment_method, status, created_at)
    VALUES (1, N'Nguyen Van Huy', '0987654321', N'So 1 Vo Van Ngan, TP Thu Duc', N'Khach yeu cau doi dia chi nhan', 125.00, 'COD', 'CANCELLED', DATEADD(DAY, -2, GETDATE()));
    
    DECLARE @new_id7 int = SCOPE_IDENTITY();
    INSERT INTO dbo.order_details (order_id, bookid, book_title, quantity, unit_price, subtotal)
    VALUES (@new_id7, 1, 'The Quiet Library', 1, 125.00, 125.00);
END;

IF NOT EXISTS (SELECT 1 FROM dbo.orders WHERE status = 'RETURNED')
BEGIN
    INSERT INTO dbo.orders (user_id, customer_name, phone, shipping_address, note, total_amount, payment_method, status, created_at)
    VALUES (1, N'Nguyen Van Huy', '0987654321', N'So 1 Vo Van Ngan, TP Thu Duc', N'Khong lien lac duoc nguoi nhan 3 lan', 150.00, 'COD', 'RETURNED', DATEADD(DAY, -3, GETDATE()));
    
    DECLARE @new_id8 int = SCOPE_IDENTITY();
    INSERT INTO dbo.order_details (order_id, bookid, book_title, quantity, unit_price, subtotal)
    VALUES (@new_id8, 3, 'Bong Thuyen Tren Song', 1, 150.00, 150.00);
END;

-- Kiem tra danh sach don hang va trang thai sau khi cap nhat:
SELECT order_id, user_id, customer_name, total_amount, status, created_at 
FROM dbo.orders 
ORDER BY order_id ASC;
GO
