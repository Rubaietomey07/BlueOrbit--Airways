package com.example.airlinemanagementsystem.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/airline_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "root123";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
            return connection;
        } catch (SQLException e) {
            System.err.println("=== DATABASE CONNECTION FAILED ===");
            System.err.println("Reason: " + e.getMessage());
            System.err.println("==================================");
        } catch (ClassNotFoundException e) {
            System.err.println("=== DRIVER NOT FOUND ===");
            System.err.println("MySQL driver missing!");
            System.err.println("========================");
        }
        return null;
    }
}