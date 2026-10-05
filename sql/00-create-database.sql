-- SQL Server 2019+; UTF-8 preserves Vietnamese in the original varchar columns.
-- Legacy text columns must override the collation (see 01-schema.sql).
-- An existing database is never dropped or recreated.
IF DB_ID(N'HNHBOOKSTORE') IS NULL
    EXEC(N'CREATE DATABASE HNHBOOKSTORE COLLATE Latin1_General_100_CI_AS_SC_UTF8');
GO
