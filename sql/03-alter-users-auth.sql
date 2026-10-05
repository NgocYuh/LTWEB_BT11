-- Cau 2: Bo sung cot phu cho users va seed admin mau
-- Khong sua cot goc, khong tao lai database.
SET XACT_ABORT ON;

IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID('dbo.users') AND name = 'is_verified'
)
BEGIN
    ALTER TABLE dbo.users ADD is_verified bit NOT NULL CONSTRAINT DF_users_is_verified DEFAULT 0;
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.columns 
    WHERE object_id = OBJECT_ID('dbo.users') AND name = 'password_salt'
)
BEGIN
    ALTER TABLE dbo.users ADD password_salt varchar(64) NULL;
END;
GO

-- Seed tai khoan admin mau (mat khau Admin@24133023 da bam PBKDF2-HMAC-SHA256 128-bit)
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = 'admin@hnhbookstore.vn')
BEGIN
    INSERT INTO dbo.users (email, fullname, phone, passwd, password_salt, signup_date, last_login, is_admin, is_verified)
    VALUES (
        'admin@hnhbookstore.vn',
        N'Quản trị viên',
        24133023,
        '78712a9c1cb722a38f6cedcb73e49923',
        '9f2585a3a6661a93d4765f593738b089',
        GETDATE(),
        NULL,
        1,
        1
    );
END;
GO

