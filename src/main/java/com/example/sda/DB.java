package com.example.sda;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    private static final String URL = "jdbc:mysql://ballast.proxy.rlwy.net:58435/railway";
    private static final String USER = "root";
    private static final String PASS = "ZCeApuKjLIXyKlsIhenclIgdxrojOyUn";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected successfully!");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
