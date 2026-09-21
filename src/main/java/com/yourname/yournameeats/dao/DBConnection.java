package com.yourname.yournameeats.dao; // (or your specific package name)

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load H2 driver
                Class.forName("org.h2.Driver");
                
                // Use a portable relative path for file-based H2 database
                // This works on Windows locally and Linux on Render
                String jdbcURL = "jdbc:h2:file:./zomato_db;AUTO_SERVER=TRUE";
                String user = "sa";
                String password = "";
                
                connection = DriverManager.getConnection(jdbcURL, user, password);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}