-- Chức năng thanh toán COD: Bảng orders và order_details
SET XACT_ABORT ON;

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'orders' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.orders (
        order_id int IDENTITY(1,1) NOT NULL,
        user_id int NOT NULL,
        customer_name nvarchar(100) NOT NULL,
        phone varchar(20) NOT NULL,
        shipping_address nvarchar(255) NOT NULL,
        note nvarchar(500) NULL,
        total_amount decimal(10,2) NOT NULL,
        payment_method varchar(20) NOT NULL CONSTRAINT DF_orders_payment_method DEFAULT 'COD',
        status varchar(30) NOT NULL CONSTRAINT DF_orders_status DEFAULT 'PENDING',
        created_at datetime NOT NULL CONSTRAINT DF_orders_created_at DEFAULT GETDATE(),
        CONSTRAINT PK_orders PRIMARY KEY (order_id),
        CONSTRAINT FK_orders_users FOREIGN KEY (user_id) REFERENCES dbo.users(id)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'order_details' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.order_details (
        detail_id int IDENTITY(1,1) NOT NULL,
        order_id int NOT NULL,
        bookid int NOT NULL,
        book_title varchar(200) NULL,
        quantity int NOT NULL,
        unit_price decimal(6,2) NOT NULL,
        subtotal decimal(10,2) NOT NULL,
        CONSTRAINT PK_order_details PRIMARY KEY (detail_id),
        CONSTRAINT FK_order_details_orders FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id) ON DELETE CASCADE,
        CONSTRAINT FK_order_details_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid)
    );
END;
GO
