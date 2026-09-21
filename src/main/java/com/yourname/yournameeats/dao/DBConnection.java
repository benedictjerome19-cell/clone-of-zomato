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
                
                // Portable relative path for H2 database
                String jdbcURL = "jdbc:h2:file:./data/zomatodb;AUTO_SERVER=TRUE";
                String user = "sa";
                String password = "";
                
                connection = DriverManager.getConnection(jdbcURL, user, password);
                
                // Initialize tables on startup
                initializeDatabase(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Comprehensive users table schema covering all standard registration fields
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "email VARCHAR(255) UNIQUE, " +
                    "password VARCHAR(255), " +
                    "phone VARCHAR(50), " +
                    "address VARCHAR(500), " +
                    "role VARCHAR(50))";
            stmt.execute(createUsersTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}