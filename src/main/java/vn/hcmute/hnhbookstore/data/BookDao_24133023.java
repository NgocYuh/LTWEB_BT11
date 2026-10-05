package vn.hcmute.hnhbookstore.data;

import vn.hcmute.hnhbookstore.model.Author_24133023;
import vn.hcmute.hnhbookstore.model.Book_24133023;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BookDao_24133023 {

    public List<Book_24133023> findBooksByAuthor(int authorId, int page, int pageSize) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 3;
        int offset = (page - 1) * pageSize;

        String sql = """
            SELECT b.bookid, b.isbn, b.title, b.publisher, b.price, b.description, b.publish_date, b.cover_image, b.quantity,
                   (SELECT COUNT(*) FROM dbo.rating r WHERE r.bookid = b.bookid) AS review_count
            FROM dbo.books b
            JOIN dbo.book_author ba ON ba.bookid = b.bookid
            WHERE ba.author_id = ?
            ORDER BY b.bookid ASC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        List<Book_24133023> books = new ArrayList<>();
        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            stmt.setInt(2, offset);
            stmt.setInt(3, pageSize);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        }

        loadAuthorsForBooks(books);
        return books;
    }

    public int countBooksByAuthor(int authorId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT ba.bookid) AS total FROM dbo.book_author ba WHERE ba.author_id = ?";
        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public Book_24133023 findById(int bookId) throws SQLException {
        String sql = """
            SELECT b.bookid, b.isbn, b.title, b.publisher, b.price, b.description, b.publish_date, b.cover_image, b.quantity,
                   (SELECT COUNT(*) FROM dbo.rating r WHERE r.bookid = b.bookid) AS review_count
            FROM dbo.books b
            WHERE b.bookid = ?
            """;
        Book_24133023 book = null;
        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    book = mapBook(rs);
                }
            }
        }

        if (book != null) {
            List<Book_24133023> singleList = new ArrayList<>();
            singleList.add(book);
            loadAuthorsForBooks(singleList);
        }
        return book;
    }

    public List<Book_24133023> findAllBooksPaged(int page, int pageSize) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;

        String sql = """
            SELECT b.bookid, b.isbn, b.title, b.publisher, b.price, b.description, b.publish_date, b.cover_image, b.quantity,
                   (SELECT COUNT(*) FROM dbo.rating r WHERE r.bookid = b.bookid) AS review_count
            FROM dbo.books b
            ORDER BY b.bookid ASC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        List<Book_24133023> books = new ArrayList<>();
        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offset);
            stmt.setInt(2, pageSize);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        }

        loadAuthorsForBooks(books);
        return books;
    }

    public int countTotalBooks() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM dbo.books";
        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public int insertBook(Book_24133023 book, int authorId) throws SQLException {
        String insertBookSql = """
            INSERT INTO dbo.books (isbn, title, publisher, price, description, publish_date, cover_image, quantity)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        String insertAuthorSql = "INSERT INTO dbo.book_author (bookid, author_id) VALUES (?, ?)";

        try (var conn = ConnectionFactory_24133023.open()) {
            conn.setAutoCommit(false);
            try {
                int generatedBookId;
                try (var stmt = conn.prepareStatement(insertBookSql, Statement.RETURN_GENERATED_KEYS)) {
                    bindBookParams(stmt, book);
                    stmt.executeUpdate();
                    try (var keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            generatedBookId = keys.getInt(1);
                            book.setBookid(generatedBookId);
                        } else {
                            throw new SQLException("Insert book failed, no ID obtained.");
                        }
                    }
                }

                try (var authorStmt = conn.prepareStatement(insertAuthorSql)) {
                    authorStmt.setInt(1, generatedBookId);
                    authorStmt.setInt(2, authorId);
                    authorStmt.executeUpdate();
                }

                conn.commit();
                return generatedBookId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateBook(Book_24133023 book, int authorId) throws SQLException {
        String updateBookSql = """
            UPDATE dbo.books
            SET isbn = ?, title = ?, publisher = ?, price = ?, description = ?,
                publish_date = ?, cover_image = ?, quantity = ?
            WHERE bookid = ?
            """;
        String deleteAuthorSql = "DELETE FROM dbo.book_author WHERE bookid = ?";
        String insertAuthorSql = "INSERT INTO dbo.book_author (bookid, author_id) VALUES (?, ?)";

        try (var conn = ConnectionFactory_24133023.open()) {
            conn.setAutoCommit(false);
            try {
                try (var stmt = conn.prepareStatement(updateBookSql)) {
                    bindBookParams(stmt, book);
                    stmt.setInt(9, book.getBookid());
                    stmt.executeUpdate();
                }

                try (var delStmt = conn.prepareStatement(deleteAuthorSql)) {
                    delStmt.setInt(1, book.getBookid());
                    delStmt.executeUpdate();
                }

                try (var insStmt = conn.prepareStatement(insertAuthorSql)) {
                    insStmt.setInt(1, book.getBookid());
                    insStmt.setInt(2, authorId);
                    insStmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void deleteBook(int bookId) throws SQLException {
        String deleteRatings = "DELETE FROM dbo.rating WHERE bookid = ?";
        String deleteBookAuthors = "DELETE FROM dbo.book_author WHERE bookid = ?";
        String deleteBook = "DELETE FROM dbo.books WHERE bookid = ?";

        try (var conn = ConnectionFactory_24133023.open()) {
            conn.setAutoCommit(false);
            try {
                try (var s1 = conn.prepareStatement(deleteRatings)) {
                    s1.setInt(1, bookId);
                    s1.executeUpdate();
                }
                try (var s2 = conn.prepareStatement(deleteBookAuthors)) {
                    s2.setInt(1, bookId);
                    s2.executeUpdate();
                }
                try (var s3 = conn.prepareStatement(deleteBook)) {
                    s3.setInt(1, bookId);
                    int affected = s3.executeUpdate();
                    if (affected == 0) {
                        throw new SQLException("Sách cần xóa không tồn tại hoặc đã bị xóa.");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void bindBookParams(PreparedStatement stmt, Book_24133023 book) throws SQLException {
        if (book.getIsbn() != null) {
            stmt.setInt(1, book.getIsbn());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }
        stmt.setString(2, book.getTitle());
        stmt.setString(3, book.getPublisher());
        if (book.getPrice() != null) {
            stmt.setBigDecimal(4, book.getPrice());
        } else {
            stmt.setNull(4, Types.DECIMAL);
        }
        stmt.setString(5, book.getDescription());
        if (book.getPublishDate() != null) {
            stmt.setDate(6, Date.valueOf(book.getPublishDate()));
        } else {
            stmt.setNull(6, Types.DATE);
        }
        stmt.setString(7, book.getCoverImage());
        if (book.getQuantity() != null) {
            stmt.setInt(8, book.getQuantity());
        } else {
            stmt.setNull(8, Types.INTEGER);
        }
    }

    private Book_24133023 mapBook(ResultSet rs) throws SQLException {
        int bookid = rs.getInt("bookid");
        int isbnVal = rs.getInt("isbn");
        Integer isbn = rs.wasNull() ? null : isbnVal;
        String title = rs.getString("title");
        String publisher = rs.getString("publisher");
        var price = rs.getBigDecimal("price");
        String description = rs.getString("description");
        Date pubDate = rs.getDate("publish_date");
        String coverImage = rs.getString("cover_image");
        int qtyVal = rs.getInt("quantity");
        Integer quantity = rs.wasNull() ? null : qtyVal;
        int reviewCount = rs.getInt("review_count");

        return new Book_24133023(
                bookid,
                isbn,
                title,
                publisher,
                price,
                description,
                pubDate == null ? null : pubDate.toLocalDate(),
                coverImage,
                quantity,
                reviewCount
        );
    }

    private void loadAuthorsForBooks(List<Book_24133023> books) throws SQLException {
        if (books == null || books.isEmpty()) {
            return;
        }

        Map<Integer, Book_24133023> map = new HashMap<>();
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < books.size(); i++) {
            map.put(books.get(i).getBookid(), books.get(i));
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sql = "SELECT ba.bookid, a.author_id, a.author_name, a.date_of_birth " +
                     "FROM dbo.book_author ba JOIN dbo.author a ON a.author_id = ba.author_id " +
                     "WHERE ba.bookid IN (" + placeholders + ") ORDER BY a.author_id ASC";

        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            for (Book_24133023 b : books) {
                stmt.setInt(idx++, b.getBookid());
            }
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bId = rs.getInt("bookid");
                    int aId = rs.getInt("author_id");
                    String aName = rs.getString("author_name");
                    Date birth = rs.getDate("date_of_birth");
                    Author_24133023 author = new Author_24133023(aId, aName, birth == null ? null : birth.toLocalDate(), 0);
                    Book_24133023 target = map.get(bId);
                    if (target != null) {
                        target.getAuthors().add(author);
                    }
                }
            }
        }
    }
}
