package com.yourname.yournameeats.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load H2 JDBC Driver
                Class.forName("org.h2.Driver");
                
                // Portable relative file path for cloud and local environments
                String jdbcURL = "jdbc:h2:file:./data/zomatodb;AUTO_SERVER=TRUE";
                String user = "sa";
                String password = "";

                connection = DriverManager.getConnection(jdbcURL, user, password);
                
                // Automatically create tables on connection initialization
                initializeDatabase(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Creates the users table with all required columns for registration and email lookups
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "email VARCHAR(255) UNIQUE, " +
                    "password VARCHAR(255), " +
                    "role VARCHAR(50))";
            stmt.execute(createUsersTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}