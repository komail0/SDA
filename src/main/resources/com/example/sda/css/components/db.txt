
package com.example.sda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to manage the database connection pool/instance.
 * NOTE: For production, you should use a proper connection pooling library.
 */
public class DB {
    // TODO: Replace with actual connection details, perhaps loaded from a database.properties file
    private static final String JDBC_URL = "jdbc:mysql://ballast.proxy.rlwy.net:58435/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "ZCeApuKjLIXyKlsIhenclIgdxrojOyUn";

    // Static block to load the JDBC driver once
    static {
        try {
            // Ensure this driver is in your pom.xml dependencies!
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Check your pom.xml and classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes and returns a new database connection.
     * @return A valid SQL Connection object.
     * @throws SQLException if a database access error occurs or the URL is null.
     */
    public static Connection getConnection() throws SQLException {
        // In a real application, you would use a connection pool (like HikariCP) here.
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}