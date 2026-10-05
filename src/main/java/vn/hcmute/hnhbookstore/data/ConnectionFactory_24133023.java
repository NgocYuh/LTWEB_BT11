package vn.hcmute.hnhbookstore.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionFactory_24133023 {
    private ConnectionFactory_24133023() { }

    public static Connection open() throws SQLException {
        // Tomcat's initial DriverManager scan cannot see drivers inside a WAR.
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("Microsoft JDBC driver is missing from WEB-INF/lib.", exception);
        }
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            throw new SQLException("DB_URL has not been configured for HNHBOOKSTORE.");
        }
        Properties properties = new Properties();
        String username = System.getenv("DB_USERNAME");
        if (username != null && !username.isBlank()) {
            properties.setProperty("user", username);
            String password = System.getenv("DB_PASSWORD");
            if (password == null || password.isBlank()) {
                throw new SQLException("DB_PASSWORD has not been configured.");
            }
            properties.setProperty("password", password);
        }
        properties.setProperty("loginTimeout", "5");
        properties.setProperty("socketTimeout", "15000");
        properties.setProperty("applicationName", "HNHBOOKSTORE");
        return DriverManager.getConnection(url, properties);
    }
}
