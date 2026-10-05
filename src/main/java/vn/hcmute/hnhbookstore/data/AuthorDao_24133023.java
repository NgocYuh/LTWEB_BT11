package vn.hcmute.hnhbookstore.data;

import vn.hcmute.hnhbookstore.model.Author_24133023;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class AuthorDao_24133023 {
    public List<Author_24133023> findAll() throws SQLException {
        String sql = """
            SELECT a.author_id, a.author_name, a.date_of_birth, COUNT(ba.bookid) AS book_count
            FROM dbo.author a LEFT JOIN dbo.book_author ba ON ba.author_id = a.author_id
            GROUP BY a.author_id, a.author_name, a.date_of_birth ORDER BY a.author_id
            """;
        try (var connection = ConnectionFactory_24133023.open();
             var statement = connection.prepareStatement(sql);
             var rows = statement.executeQuery()) {
            List<Author_24133023> authors = new ArrayList<>();
            while (rows.next()) {
                var birth = rows.getDate("date_of_birth");
                authors.add(new Author_24133023(rows.getInt("author_id"), rows.getString("author_name"),
                    birth == null ? null : birth.toLocalDate(), rows.getInt("book_count")));
            }
            return authors;
        }
    }
}
