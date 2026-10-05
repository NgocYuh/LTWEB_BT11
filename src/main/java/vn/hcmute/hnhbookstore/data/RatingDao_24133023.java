package vn.hcmute.hnhbookstore.data;

import vn.hcmute.hnhbookstore.model.Rating_24133023;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class RatingDao_24133023 {

    public List<Rating_24133023> findRatingsByBookId(int bookId) throws SQLException {
        String sql = """
            SELECT r.userid, COALESCE(u.fullname, u.email) AS user_name, r.bookid, r.rating, r.review_text
            FROM dbo.rating r
            JOIN dbo.users u ON u.id = r.userid
            WHERE r.bookid = ?
            ORDER BY r.userid ASC
            """;

        List<Rating_24133023> ratings = new ArrayList<>();
        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapRating(rs));
                }
            }
        }
        return ratings;
    }

    public Rating_24133023 findUserRating(int userId, int bookId) throws SQLException {
        String sql = """
            SELECT r.userid, COALESCE(u.fullname, u.email) AS user_name, r.bookid, r.rating, r.review_text
            FROM dbo.rating r
            JOIN dbo.users u ON u.id = r.userid
            WHERE r.userid = ? AND r.bookid = ?
            """;
        try (var conn = ConnectionFactory_24133023.open();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRating(rs);
                }
            }
        }
        return null;
    }

    public void upsertRating(int userId, int bookId, Integer ratingScore, String reviewText) throws SQLException {
        String checkSql = "SELECT 1 FROM dbo.rating WHERE userid = ? AND bookid = ?";
        String updateSql = "UPDATE dbo.rating SET rating = ?, review_text = ? WHERE userid = ? AND bookid = ?";
        String insertSql = "INSERT INTO dbo.rating (userid, bookid, rating, review_text) VALUES (?, ?, ?, ?)";

        try (var conn = ConnectionFactory_24133023.open()) {
            boolean exists = false;
            try (var checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, bookId);
                try (var rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (var updateStmt = conn.prepareStatement(updateSql)) {
                    if (ratingScore != null) {
                        updateStmt.setInt(1, ratingScore);
                    } else {
                        updateStmt.setNull(1, Types.TINYINT);
                    }
                    updateStmt.setString(2, reviewText);
                    updateStmt.setInt(3, userId);
                    updateStmt.setInt(4, bookId);
                    updateStmt.executeUpdate();
                }
            } else {
                try (var insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, bookId);
                    if (ratingScore != null) {
                        insertStmt.setInt(3, ratingScore);
                    } else {
                        insertStmt.setNull(3, Types.TINYINT);
                    }
                    insertStmt.setString(4, reviewText);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private Rating_24133023 mapRating(ResultSet rs) throws SQLException {
        int userId = rs.getInt("userid");
        String userName = rs.getString("user_name");
        int bookId = rs.getInt("bookid");
        int scoreVal = rs.getInt("rating");
        Integer rating = rs.wasNull() ? null : scoreVal;
        String reviewText = rs.getString("review_text");

        return new Rating_24133023(userId, userName, bookId, rating, reviewText);
    }
}

