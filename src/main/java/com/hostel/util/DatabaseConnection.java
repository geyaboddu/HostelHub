package com.hostel.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {

        try {

            Class.forName("org.postgresql.Driver");

            String url =
                    "jdbc:postgresql://localhost:5432/hostel_db";

            String username = "postgres";
            String password = "MyFirstProject";

            System.out.println("Connecting to local PostgreSQL...");

            Connection con =
                    DriverManager.getConnection(
                            url,
                            username,
                            password
                    );

            System.out.println(
                    "PostgreSQL connected successfully!"
            );

            return con;

        } catch (Exception e) {

            System.out.println("DATABASE ERROR:");
            e.printStackTrace();

            return null;
        }
    }
}