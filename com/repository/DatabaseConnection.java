package com.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Database connection helper
public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {}
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_db", "root", "password");
    }
}
