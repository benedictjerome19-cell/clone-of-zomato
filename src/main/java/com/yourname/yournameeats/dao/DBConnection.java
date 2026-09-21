package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load H2 driver
                Class.forName("org.h2.Driver");
                
                // Use a portable relative path that works on both Windows and Linux containers
                String jdbcURL = "jdbc:h2:file:./data/zomatodb;AUTO_SERVER=TRUE";
                String user = "sa";
                String password = "";
                
                connection = DriverManager.getConnection(jdbcURL, user, password);
                
                // Automatically create tables if they don't exist yet (prevents "Table not found" errors)
                initializeDatabase(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Create users table for registration and login
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "fullname VARCHAR(255), " +
                    "email VARCHAR(255) UNIQUE, " +
                    "password VARCHAR(255), " +
                    "role VARCHAR(50))";
            stmt.execute(createUsersTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}