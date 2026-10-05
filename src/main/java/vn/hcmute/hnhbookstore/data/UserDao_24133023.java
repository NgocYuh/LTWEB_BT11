package vn.hcmute.hnhbookstore.data;

import vn.hcmute.hnhbookstore.model.User_24133023;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

public final class UserDao_24133023 {

    public User_24133023 findByEmail(String email) throws SQLException {
        if (email == null) {
            return null;
        }
        String sql = """
            SELECT id, email, fullname, phone, passwd, password_salt, signup_date, last_login, is_admin, is_verified
            FROM dbo.users
            WHERE LOWER(email) = LOWER(?)
            """;
        try (var connection = ConnectionFactory_24133023.open();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());
            try (var rows = statement.executeQuery()) {
                if (rows.next()) {
                    return mapUser(rows);
                }
            }
        }
        return null;
    }

    public User_24133023 findById(int id) throws SQLException {
        String sql = """
            SELECT id, email, fullname, phone, passwd, password_salt, signup_date, last_login, is_admin, is_verified
            FROM dbo.users
            WHERE id = ?
            """;
        try (var connection = ConnectionFactory_24133023.open();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (var rows = statement.executeQuery()) {
                if (rows.next()) {
                    return mapUser(rows);
                }
            }
        }
        return null;
    }

    public int insert(User_24133023 user) throws SQLException {
        String sql = """
            INSERT INTO dbo.users (email, fullname, phone, passwd, password_salt, signup_date, is_admin, is_verified)
            VALUES (?, ?, ?, ?, ?, GETDATE(), ?, ?)
            """;
        try (var connection = ConnectionFactory_24133023.open();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getEmail().trim());
            statement.setNString(2, user.getFullname());
            if (user.getPhone() != null) {
                statement.setInt(3, user.getPhone());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            statement.setString(4, user.getPasswd());
            statement.setString(5, user.getPasswordSalt());
            statement.setBoolean(6, user.isAdmin());
            statement.setBoolean(7, user.isVerified());

            int affected = statement.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Insert user failed, no rows affected.");
            }
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    user.setId(id);
                    return id;
                } else {
                    throw new SQLException("Insert user failed, no ID obtained.");
                }
            }
        }
    }

    public void markVerified(int id) throws SQLException {
        String sql = "UPDATE dbo.users SET is_verified = 1 WHERE id = ?";
        try (var connection = ConnectionFactory_24133023.open();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public void updateLastLogin(int id) throws SQLException {
        String sql = "UPDATE dbo.users SET last_login = GETDATE() WHERE id = ?";
        try (var connection = ConnectionFactory_24133023.open();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private User_24133023 mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String email = rs.getString("email");
        String fullname = rs.getNString("fullname");
        int phoneVal = rs.getInt("phone");
        Integer phone = rs.wasNull() ? null : phoneVal;
        String passwd = rs.getString("passwd");
        String passwordSalt = rs.getString("password_salt");
        Timestamp signupTs = rs.getTimestamp("signup_date");
        Timestamp lastLoginTs = rs.getTimestamp("last_login");
        boolean isAdmin = rs.getBoolean("is_admin");
        boolean isVerified = rs.getBoolean("is_verified");

        return new User_24133023(
                id,
                email,
                fullname,
                phone,
                passwd,
                passwordSalt,
                signupTs == null ? null : signupTs.toLocalDateTime(),
                lastLoginTs == null ? null : lastLoginTs.toLocalDateTime(),
                isAdmin,
                isVerified
        );
    }
}

