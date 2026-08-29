package com.hostel.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {

        try {
            String host = System.getenv("PGHOST");
            String port = System.getenv("PGPORT");
            String database = System.getenv("PGDATABASE");
            String username = System.getenv("PGUSER");
            String password = System.getenv("PGPASSWORD");

            if (host == null || port == null || database == null
                    || username == null || password == null) {

                System.out.println("PostgreSQL environment variables are missing!");
                return null;
            }

            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql://" + host + ":" + port
                    + "/" + database + "?sslmode=require";

            System.out.println("Connecting to PostgreSQL...");

            Connection con = DriverManager.getConnection(
                    url,
                    username,
                    password
            );

            System.out.println("PostgreSQL connected successfully!");

            return con;

        } catch (Exception e) {

            System.out.println("DATABASE ERROR:");
            e.printStackTrace();

            return null;
        }
    }
}